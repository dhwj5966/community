package com.starry.community.service.impl;

import com.starry.community.bean.Message;
import com.starry.community.mapper.MessageMapper;
import com.starry.community.service.MessageService;
import com.starry.community.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-12-1:14 PM
 * @Describe
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    @Override
    public Message findLatestNotificationByUserIdAndTopic(int userId, String topic) {
        return messageMapper.selectLatestNotificationByUserIdAndTopic(userId, topic);
    }

    @Override
    public int findNotificationsCountByUserIdAndTopic(int userId, String topic) {
        return messageMapper.selectNotificationsCountByUserIdAndTopic(userId, topic);
    }

    @Override
    public int findUnreadNotificationsCountByUserIdAndTopic(int userId, String topic) {
        return messageMapper.selectUnreadNotificationsCountByUserIdAndTopic(userId, topic);
    }

    @Override
    public int addMessage(Message message) {
        if (message == null) {
            throw new RuntimeException();
        }
        message.setContent(sensitiveWordsFilter.filter(HtmlUtils.htmlEscape(message.getContent())));
        return messageMapper.insertMessage(message);
    }

    @Override
    public int updateMessagesStatus(List<Integer> ids, int status) {
        if (ids == null) {
            throw new RuntimeException();
        }
        return messageMapper.updateStatusById(ids, status);
    }

    @Override
    public int readMessagesStatus(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return messageMapper.updateStatusById(ids, 1);
    }

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int findConversationsRows(int userId) {
        return messageMapper.selectConversationsRows(userId);
    }

    @Override
    public List<Message> findMessagesByConversationId(String conversationId, int offset, int limit) {
        return messageMapper.selectMessagesByConversationId(conversationId,offset,limit);
    }

    @Override
    public int findMessagesRowsByConversationId(String conversationId) {
        return messageMapper.selectMessagesRowsByConversationId(conversationId);
    }

    @Override
    public int findUnreadMessagesCount(int userId, String conversationId) {
        return messageMapper.selectUnreadMessagesCount(userId,conversationId);
    }
}
