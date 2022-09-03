package com.starry.community.service.impl;

import com.starry.community.bean.User;
import com.starry.community.mapper.UserMapper;
import com.starry.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Starry
 * @create 2022-09-02-6:18 PM
 * @Describe
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
