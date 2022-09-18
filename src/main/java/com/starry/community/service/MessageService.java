package com.starry.community.service;

import com.starry.community.bean.Message;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-12-1:13 PM
 * @Describe
 */
public interface MessageService {

    /**
     * 查询某个user的某个topic的最新的一条通知
     * @param userId user的id
     * @param topic 要查询的topic
     * @return 如果不存在该topic的通知，则返回null
     */
    Message findLatestNotificationByUserIdAndTopic(int userId,String topic);

    /**
     * 查询某个user的某个topic的所有通知的数量
     * @param userId user的id
     * @param topic 要查询的topic
     * @return
     */
    int findNotificationsCountByUserIdAndTopic(int userId, String topic);

    /**
     * 查询某个user的某个topic的所有未读通知的数量,
     * @param userId user的id
     * @param topic 要查询的topic，如果topic为null，则查询该用户所有主题的未读消息总数
     * @return
     */
    int findUnreadNotificationsCountByUserIdAndTopic(int userId, String topic);


    /**
     * 对message的content进行敏感词过滤，并将message添加到数据库
     * @param message
     * @return
     */
    int addMessage(Message message);

    /**
     * 将ids中的所有id对应的message的status字段修改为status
     * @return
     */
    int updateMessagesStatus(List<Integer> ids, int status);

    /**
     * 将ids中的所有id对应的message的status字段修改为1(已读)
     * @param ids
     * @return
     */
    int readMessagesStatus(List<Integer> ids);

    /**
     * 查询用户的所有会话，用一个Message代表一个会话，该Message是该用户参与的会话中最新的一个Message
     * offset和limit用来支持分页功能
     * @param userId
     * @param offset
     * @param limit
     */
    List<Message> findConversations(int userId, int offset, int limit);

    /**
     * 根据用户id，查询该用户共有多少个会话
     * @return 指定用户的会话数量
     */
    int findConversationsRows(int userId);

    /**
     * 根据conversationId，查找对应的会话包含的所有message
     * offset和limit用来支持分页功能
     * @param conversationId
     * @param offset
     * @param limit
     */
    List<Message> findMessagesByConversationId(String conversationId,int offset,int limit);

    /**
     * 根据conversationId，查找对应会话包含的message数量
     * @param conversationId
     * @return 指定会话的message数量
     */
    int findMessagesRowsByConversationId(String conversationId);

    /**
     * 根据userId和conversationId查询特定用户的特定会话的未读信息数量
     * @param userId 用来指定用户
     * @param conversationId 用来指定会话，可以为null，如果为null则查询指定用户的所有未读信息数量
     */
    int findUnreadMessagesCount(int userId, String conversationId);
}
