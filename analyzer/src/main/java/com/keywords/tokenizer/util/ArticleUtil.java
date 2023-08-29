package com.keywords.tokenizer.util;

import com.keywords.tokenizer.consts.ArticleRow;
import com.keywords.tokenizer.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wangyue
 * @date 2023/8/9
 */
@Slf4j
public class ArticleUtil {
    public static String getAnalysisTime(String path, String rowName, String type) {
        File dataFolder = new File(path);
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            return null;
        }
        String year=dataFolder.listFiles()[0].getName().substring(0, 4);
        String month=dataFolder.listFiles()[0].getName().substring(4, 6);
        return year+"-"+month;
    }

    public static List<JavaRDD<String>> getRDDList(JavaSparkContext sc, String rowName, String path) {
        List<JavaRDD<String>> RDDList = new ArrayList<>();
        File dataFolder = new File(path);
        if (dataFolder.exists() && dataFolder.isDirectory()) {
            File[] files = dataFolder.listFiles();
            for (File file : files) {
                JavaRDD<String> articles = getRDDByFile(sc, rowName, file.getPath());
                RDDList.add(articles);
            }
        } else {
            log.error("指定的文件夹不存在");
        }
        return RDDList;
    }

    private static JavaRDD<String> getRDDByFile(JavaSparkContext sc, String rowName, String path) {
        // 读取文件，返回JavaRDD<String>，其中每个元素是一行
        JavaRDD<String> lines = sc.textFile(path);
        StringBuilder sb = new StringBuilder();
        JavaRDD<String> segments = lines.flatMap(line -> {
            if (line.equals("<REC>")) {
                if (sb.length() != 0) {
                    List<String> segmentList = new ArrayList<>();
                    segmentList.add(sb.toString());
                    sb.setLength(0);
                    return segmentList.iterator();
                }
            } else {
                sb.append(line);
            }
            return Collections.emptyIterator();
        });
        JavaRDD<String> strRDD = segments.map(segment -> parseStr(rowName, segment)).filter(s -> !s.isEmpty());
        return strRDD;
    }

    public static String parseStr(String rowName, String segment) {
        String str = "";
        if (rowName.equals(ArticleRow.TITLE)) {
            // 标题
            int titleStartIndex = segment.indexOf("<IR_TITLE>=") + "<IR_TITLE>=".length();
            int titleEndIndex = segment.indexOf("<IR_AUTHOR>=");
            try {
                str = segment.substring(titleStartIndex, titleEndIndex);
            } catch (StringIndexOutOfBoundsException e) {
//                log.error("未找到标题");
                str = "";
            }
        } else {
            // 内容
            int contentStartIndex = segment.indexOf("<IR_CONTENT>=") + "<IR_CONTENT>=".length();
            int contentEndIndex = segment.indexOf("<IR_DOWNLOAD_URL>");
            try {
                str = segment.substring(contentStartIndex, contentEndIndex);

                // 去除 <IMAGE> 标签
                int startIndex = str.indexOf("<IMAGE");
                while (startIndex != -1) {
                    int endIndex = str.indexOf(">", startIndex);
                    if (endIndex != -1) {
                        str = str.substring(0, startIndex) + str.substring(endIndex + 1);
                    }
                    startIndex = str.indexOf("<IMAGE", startIndex);
                }
            } catch (StringIndexOutOfBoundsException e) {
//                log.error("未找到内容");
                str = "";
            }
        }
        return str;
    }


    ////////////////////////////////////////////////////
    //               以下为旧版，返回文章列表             //
    ///////////////////////////////////////////////////

//    public static List<List<Article>> getArticleList(String folderPath) {
//        List<List<Article>> articleList = new ArrayList<>();
//        File dataFolder = new File(folderPath);
//        if (dataFolder.exists() && dataFolder.isDirectory()) {
//            File[] files = dataFolder.listFiles();
//            for (File file : files) {
//                List<Article> articles = getArticleByFile(file.getPath());
//                articleList.add(articles);
//            }
//        } else {
//            log.error("指定的文件夹不存在");
//        }
//        return articleList;
//    }
//
//    public static List<Article> getArticleByFile(String filePath) {
//        SparkSession spark = SparkSession.builder().appName("ArticleReader").getOrCreate();
//        JavaRDD<String> lines = spark.read().option("encoding", "GB2312").textFile(filePath).javaRDD();
//        StringBuilder sb = new StringBuilder();
//        JavaRDD<String> segments = lines.flatMap(line -> {
//            if (line.equals("<REC>")) {
//                List<String> segmentList = new ArrayList<>();
//                if (sb.length() != 0) {
//                    segmentList.add(sb.toString());
//                    sb.setLength(0);
//                    return segmentList.iterator();
//                } else {
//                    sb.append(line);
//                }
//            } else {
//                sb.append(line);
//            }
//            return Collections.emptyIterator();
//        });
//        // 调用自定义的函数，解析文章段落
//        JavaRDD<Article> articles = segments.map(ArticleUtil::parseArticle);
//        return articles.collect();
//    }

//    public static Article parseArticle(String segment) {
//        // 标题
//        int titleStartIndex = segment.indexOf("<IR_TITLE>=") + "<IR_TITLE>=".length();
//        int titleEndIndex = segment.indexOf("<IR_AUTHOR>=");
//        String title = "";
//        try {
//            title = segment.substring(titleStartIndex, titleEndIndex);
//        } catch (StringIndexOutOfBoundsException e) {
//            log.error("未找到标题");
//            title = "";
//        }
//
//        // 内容
//        int contentStartIndex = segment.indexOf("<IR_CONTENT>=") + "<IR_CONTENT>=".length();
//        int contentEndIndex = segment.indexOf("<IR_DOWNLOAD_URL>");
//        String content = "";
//        try {
//            content = segment.substring(contentStartIndex, contentEndIndex);
//
//            // 去除 <IMAGE> 标签
//            int startIndex = content.indexOf("<IMAGE");
//            while (startIndex != -1) {
//                int endIndex = content.indexOf(">", startIndex);
//                if (endIndex != -1) {
//                    content = content.substring(0, startIndex) + content.substring(endIndex + 1);
//                }
//                startIndex = content.indexOf("<IMAGE", startIndex);
//            }
//        } catch (StringIndexOutOfBoundsException e) {
//            log.error("未找到内容");
//            content = "";
//        }
//
//        return new Article(title, content, "");
//    }
}
