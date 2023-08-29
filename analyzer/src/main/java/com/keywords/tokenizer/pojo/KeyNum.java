package com.keywords.tokenizer.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wangyue
 * @date 2023/8/11
 */
@Data
public class KeyNum implements Serializable {

    @ExcelProperty("关键字")
    String name;
    @ExcelProperty("出现次数")
    Integer num;
    public KeyNum(String name, Integer num) {
        this.name = name;
        this.num = num;
    }
}
