package com.starry.community.service;

import com.starry.community.bean.User;

/**
 * @author Starry
 * @create 2022-09-02-6:18 PM
 * @Describe
 */
public interface UserService {
    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User findUserById(int id);
}
