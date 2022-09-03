package com.starry.community.mapper;

import com.starry.community.CommunityApplication;
import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.User;
import com.starry.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Starry
 * @create 2022-09-02-5:38 PM
 * @Describe
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class DiscussPostMapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private UserService userService;

    @Test
    void selectDiscussPostsByUserId() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPostsByUserId(111, 0, 3);
        discussPosts.forEach(System.out::println);
    }

    @Test
    void selectDiscussPostRows() {
        int i = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(i);
    }

    @Test
    void test(){
        User userById = userService.findUserById(0);
        System.out.println(userById);
    }
}