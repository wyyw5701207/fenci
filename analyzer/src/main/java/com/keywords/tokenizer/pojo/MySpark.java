package com.keywords.tokenizer.pojo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author wangyue
 * @date 2023/8/14
 */
@Component
public class MySpark implements CommandLineRunner {
    private SparkConf conf;
    private JavaSparkContext sc;

    public MySpark() {
        SparkConf conf = new SparkConf()
                .setAppName("WordFrequency")
                .set("spark.driver.maxResultSize", "8g")
                .setMaster("local[*]");
        this.conf=conf;
        this.sc = new JavaSparkContext(conf);
    }

    public SparkConf getSparkConf(){
        return conf;
    }

    public JavaSparkContext getSparkContext() {
        return sc;
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
