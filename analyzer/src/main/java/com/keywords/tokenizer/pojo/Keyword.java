package com.keywords.tokenizer.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author wangyue
 * @date 2023/8/15
 */
@Data
public class Keyword implements Serializable {
    public Keyword(String name, String fieldType, String rate, String analysisTime, String analyzerType, Integer wordLength,Integer fileNum) {
        this.name = name;
        this.fieldType = fieldType;
        this.rate = rate;
        this.analysisTime = analysisTime;
        this.analyzerType = analyzerType;
        this.wordLength = wordLength;
        this.fileNum=fileNum;
    }

    public Keyword() {
    }

    @ExcelProperty("关键字名称")
    String name;
    @ExcelProperty("分析内容类型")
    String fieldType;
    @ExcelProperty("频率")
    String rate;
    @ExcelProperty("分析时间")
    String analysisTime;
    @ExcelProperty("分词器类型")
    String analyzerType;
    @ExcelProperty("分词长度")
    Integer wordLength;
    @ExcelProperty("文件数量")
    Integer fileNum;
}
