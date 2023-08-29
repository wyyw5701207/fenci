package com.keywords.tokenizer.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangyue
 * @date 2023/8/9
 */
@Data
public class Article implements Serializable {
    private String title;
    private String content;
    private String abstractContent;
    public Article(String title,String content,String abstractContent){
        this.title=title;
        this.content=content;
        this.abstractContent=abstractContent;
    }
}
