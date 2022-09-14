package com.starry.community.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-11-2:57 PM
 * @Describe
 */
@Data
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    /**
     * 0: unread  1: readed  2:over time
     */
    private int status;
    private Date createTime;
}
