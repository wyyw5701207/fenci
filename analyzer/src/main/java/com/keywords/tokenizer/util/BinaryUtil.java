package com.keywords.tokenizer.util;

import com.keywords.tokenizer.pojo.KeyNum;
import com.keywords.tokenizer.pojo.MySpark;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.*;

/**
 * @author wangyue
 * @date 2023/8/10
 */
public class BinaryUtil {

    public static List<KeyNum> getKeyWord(JavaRDD<String> strRDD, int num, int wordLength) {
        JavaRDD<String> words = strRDD.flatMap(line -> splitStr(line, wordLength).iterator());
        List<KeyNum> keyNumList = SparkUtil.getKeyWordList(words, num);
        return keyNumList;
    }

    // 修改 splitStr 方法来接受分词长度作为参数
    public static List<String> splitStr(String s, int wordLength) {
        Set<String> bigrams = new HashSet<>();
        for (int i = 0; i < s.length() - wordLength + 1; i++) {
            String segment = s.substring(i, i + wordLength);
            if (isChineseAndNumber(segment)) {
                bigrams.add(segment);
            }
        }
        return new ArrayList<>(bigrams);
    }

    //    public static boolean isChineseAndNumber(String str) {
//        String regex = "^[\\u4e00-\\u9fa5]{2}$|^\\d[\\u4e00-\\u9fa5]$|^[\\u4e00-\\u9fa5]\\d$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(str);
//        return matcher.matches();
//    }
    public static boolean isChineseAndNumber(String s) {
        if (s.matches("\\d+")) {
            return false;
        }
        if (!s.matches("[\\u4E00-\\u9FA5\\d]+")) {
            return false;
        }
        return true;
    }

    private static boolean isChinese(char c) {
        return c >= 0x4e00 && c <= 0x9fa5;
    }
}
