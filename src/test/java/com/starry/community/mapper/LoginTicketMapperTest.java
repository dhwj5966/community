package com.starry.community.mapper;

import com.starry.community.CommunityApplication;
import com.starry.community.bean.LoginTicket;
import com.starry.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-05-4:17 PM
 * @Describe
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    void insertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setUserId(101);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 100000));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    void selectByTicket() {
    }

    @Test
    void updateStatus() {
    }
}