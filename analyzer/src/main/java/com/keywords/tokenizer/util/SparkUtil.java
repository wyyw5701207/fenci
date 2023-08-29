package com.keywords.tokenizer.util;

import com.keywords.tokenizer.consts.ArticleRow;
import com.keywords.tokenizer.pojo.Article;
import com.keywords.tokenizer.pojo.KeyNum;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyue
 * @date 2023/8/14
 */
public class SparkUtil {
    public static JavaRDD<String> getStrRDD(JavaSparkContext sc, List<Article> articleList,String rowName){
        JavaRDD<Article> articleRDD = sc.parallelize(articleList);
        JavaRDD<String> strRDD;
        // 提取文章内容/标题/摘要
        if (rowName.equals(ArticleRow.ABSTRACT)) {
            strRDD = articleRDD.map(Article::getAbstractContent);
        } else if (rowName.equals(ArticleRow.CONTENT)) {
            strRDD = articleRDD.map(Article::getContent);
        } else {
            strRDD = articleRDD.map(Article::getTitle);
        }
        return strRDD;
    }
    public static List<KeyNum> getKeyWordList(JavaRDD<String> words, Integer num){

        // 统计词频
        JavaPairRDD<String, Integer> wordCounts = words.mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey(Integer::sum);

        // 根据词频排序
        List<Tuple2<Integer, String>> sortedWordCounts = wordCounts.mapToPair(Tuple2::swap)
                .sortByKey(false)
                .take(num); // 取前num个高频词

        // 转换为List<KeyWord>
        List<KeyNum> result = new ArrayList<>();
        for (Tuple2<Integer, String> tuple : sortedWordCounts) {
            KeyNum keyword = new KeyNum(tuple._2(), tuple._1());
            result.add(keyword);
        }
        return result;
    }
}
