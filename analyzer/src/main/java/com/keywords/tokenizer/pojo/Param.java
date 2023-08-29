package com.keywords.tokenizer.pojo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author wangyue
 * @date 2023/8/11
 */
@Data
@Component
public class Param {
    @Value("${param.analyzerType}")
    private String analyzerType;
    @Value("${param.rowName}")
    private String rowName;
    @Value("${param.keyWordNum}")
    private Integer keyWordNum;
    @Value("${param.wordLength}")
    private Integer wordLength;
    @Value("${param.folderPath}")
    private String folderPath;
}
