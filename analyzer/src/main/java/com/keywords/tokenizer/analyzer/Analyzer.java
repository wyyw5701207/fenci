package com.keywords.tokenizer.analyzer;

import com.keywords.tokenizer.consts.AnalyzerType;
import com.keywords.tokenizer.pojo.*;
import com.keywords.tokenizer.threadpool.ThreadPoolManager;
import com.keywords.tokenizer.util.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Data
public class Analyzer {
    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static void analyzer()  {
        long startTime = System.currentTimeMillis();
        Param param = applicationContext.getBean(Param.class);
        MySpark mySpark = applicationContext.getBean(MySpark.class);
        List<JavaRDD<String>> RDDList = ArticleUtil.getRDDList(mySpark.getSparkContext(),param.getRowName(),param.getFolderPath());
        if (RDDList.isEmpty()) {
            log.error("文件为空");
            return;
        }
        boolean useBinary = param.getAnalyzerType().equals(AnalyzerType.BINARY_ANALYZER);
        // 创建一个线程池管理类的实例
        ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
        List<Future<List<KeyNum>>> futureList = new ArrayList<>();
        log.info("共 {} 线程", RDDList.size());
        AtomicInteger completedThreads = new AtomicInteger(0); // 已完成的线程数
        int total=0;
        for (JavaRDD<String> subList : RDDList) {
            total+=subList.count();
            Callable<List<KeyNum>> task = () -> {
                if (useBinary) {
                    return BinaryUtil.getKeyWord( subList, param.getKeyWordNum(),param.getWordLength());
                } else {
                    return IKUtil.getKeyWord( subList, param.getKeyWordNum(),param.getWordLength());
                }
            };
//            Future<List<KeyNum>> future = threadPoolManager.submit(task);
            Future<List<KeyNum>> future = threadPoolManager.submit(() -> {
                List<KeyNum> result = task.call();
                log.info("完成 {} 个线程", completedThreads.incrementAndGet());
                return result;
            });
            futureList.add(future);
        }
        log.info("等待分词");
        threadPoolManager.await();
        log.info("全部分词都完成了..");
        // 创建一个列表，用于存储所有任务的结果
        JavaSparkContext sc = mySpark.getSparkContext();
        JavaRDD<KeyNum> keyNumRDD = sc.emptyRDD();
        for (Future<List<KeyNum>> future : futureList) {
            try {
                List<KeyNum> subResult = future.get();
                keyNumRDD = keyNumRDD.union(sc.parallelize(subResult));
            } catch (Exception e) {
                log.error("获取任务结果出错", e);
            }
        }
        log.info("所有结果整合完成..");
        JavaPairRDD<String, Integer> nameNumPair = keyNumRDD.mapToPair(kn -> new Tuple2<>(kn.getName(), kn.getNum()));
        JavaPairRDD<String, Integer> nameSumPair = nameNumPair.reduceByKey(Integer::sum);
        JavaPairRDD<Integer, String> numNamePair = nameSumPair.mapToPair(Tuple2::swap).sortByKey(false);
        JavaRDD<KeyNum> wordMap = numNamePair.map(s -> new KeyNum(s._2, s._1));
        List<KeyNum> keyNumList = wordMap.take(param.getKeyWordNum());
        List<Keyword> list=new ArrayList<>();
        String analysisTime = ArticleUtil.getAnalysisTime(param.getFolderPath(), param.getRowName(), param.getAnalyzerType());
        Integer fileNum=new File(param.getFolderPath()).listFiles().length;
        for (KeyNum keyNum : keyNumList) {
            Keyword keyword = new Keyword(keyNum.getName(), param.getRowName(),keyNum.getNum()+"/"+total,analysisTime, param.getAnalyzerType(), param.getWordLength(),fileNum);
            list.add(keyword);
        }
        log.info("开始写入文件..");
        try {
            FileWriteUtil.write(Keyword.class,list);
        } catch (IOException e) {
            log.error("写入文件出错", e);
        }
        long endTime = System.currentTimeMillis(); // 获取方法结束时间
        long executionTimeMillis = endTime - startTime; // 计算方法执行时间（毫秒）
        double executionTimeSeconds = executionTimeMillis / 1000.0; // 转换为秒
        log.info("完成，用时：{}秒", executionTimeSeconds);
    }

}