package com.starry.community.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-01-7:51 PM
 * @Describe
 */
@Data
@Document(indexName = "discusspost", createIndex = true)
public class DiscussPost {
    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 帖子的类型。
     * 0:普通帖子
     * 1:置顶帖子
     */
    @Field(type = FieldType.Integer)
    private int type;

    /**
     * 帖子的状态。
     * 0 : 正常
     * 1 : 加精
     * 2 : 拉黑(删除)
     */
    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double)
    private double score;
}
