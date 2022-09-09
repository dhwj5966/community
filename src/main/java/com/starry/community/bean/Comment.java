package com.starry.community.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-09-1:02 AM
 * @Describe 评论对应的Java Bean
 */
@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
