package com.starry.community.mapper;

import com.starry.community.CommunityApplication;
import com.starry.community.bean.Message;
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
 * @create 2022-09-11-4:14 PM
 * @Describe
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class MessageMapperTest {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    void selectConversations() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 3);
        messages.forEach(System.out::println);
    }

    @Test
    void selectConversationsRows() {
        int i = messageMapper.selectConversationsRows(154);
        System.out.println(i);
    }

    @Test
    void selectMessagesByConversationId() {
        List<Message> messages = messageMapper.selectMessagesByConversationId("111_115", 0, 10);
        messages.forEach(System.out::println);
    }

    @Test
    void selectMessagesRowsByConversationId() {
        System.out.println(messageMapper.selectMessagesRowsByConversationId("111_116"));
    }

    @Test
    void selectUnreadMessagesCount() {
    }
}