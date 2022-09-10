package com.starry.community.bean;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Starry
 * @create 2022-09-09-1:02 AM
 * @Describe 评论对应的Java Bean
 */
@Data
public class Comment {
    private int id;
    private int userId;
    /**
     * entityType:0代表所有类型，1代表帖子的评论，2代表对帖子的评论的评论
     */
    private int entityType;
    /**
     * 对应类型数据的id
     */
    private int entityId;
    /**
     * 仅供entityType为2的数据持有，指明回复的对象（回复的帖子的id）
     */
    private int targetId;
    private String content;
    /**
     * 帖子的状态，0为正常
     */
    private int status;
    private Date createTime;

    //发布评论的用户
    private User user;
    private List<Comment> comments;
    //评论的评论数
    private int commentCount;
    private User targetUser;
}
