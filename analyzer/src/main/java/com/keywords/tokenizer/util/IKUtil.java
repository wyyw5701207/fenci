package com.keywords.tokenizer.util;

import com.keywords.tokenizer.pojo.Article;
import com.keywords.tokenizer.pojo.KeyNum;
import com.keywords.tokenizer.pojo.MySpark;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Slf4j
public class IKUtil {

    public static List<KeyNum> getKeyWord(JavaRDD<String> strRDD, int num, int wordLength) {
        JavaRDD<String> words = strRDD.flatMap(line -> {
            List<String> result = new ArrayList<>();
            // 使用IK分词器进行分词和过滤
            StringReader reader = new StringReader(line);
            IKSegmenter ikSegmenter = new IKSegmenter(reader, false);
            Lexeme lexeme;
            Set<String> uniqueWords = new HashSet<>(); // 使用Set集合来去重
            try {
                while ((lexeme = ikSegmenter.next()) != null) {
                    String word = lexeme.getLexemeText();
                    if (isValidWord(word, wordLength) && uniqueWords.add(word)) {
                        result.add(word);
                    }
                }
            } catch (IOException e) {
                log.error("",e);
            }
            return result.iterator();
        });
        List<KeyNum> keyNumList = SparkUtil.getKeyWordList(words, num);
//        sc.close();
        return keyNumList;
    }

    // 判断是否有效的词语
    private static boolean isValidWord(String word,int wordLength) {
        return word.length() == wordLength;
    }
}