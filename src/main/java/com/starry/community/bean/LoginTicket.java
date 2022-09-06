package com.starry.community.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-05-1:01 PM
 * @Describe 模拟登录凭证
 */
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
