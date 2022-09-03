package com.starry.community.mapper;

import com.starry.community.CommunityApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Starry
 * @create 2022-09-01-6:06 PM
 * @Describe
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void selectById() {
        System.out.println("bingo");
        System.out.println(userMapper.selectById(1));

    }

    @Test
    void selectByUsername() {
    }

    @Test
    void selectByEmail() {
    }

    @Test
    void insertUser() {
    }

    @Test
    void updateStatusById() {
    }

    @Test
    void updateHeaderById() {
    }

    @Test
    void updatePasswordById() {
    }
}