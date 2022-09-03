package com.starry.community.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-01-7:51 PM
 * @Describe
 */
@Data
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
