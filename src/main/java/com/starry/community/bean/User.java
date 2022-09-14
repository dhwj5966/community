package com.starry.community.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-01-4:47 PM
 * @Describe
 */
@Data
public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
