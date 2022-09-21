package com.starry.community.mapper;

import com.starry.community.bean.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-11-3:05 PM
 * @Describe
 */
@Mapper
public interface MessageMapper {

    /**
     * 查询指定user的指定topic的通知，支持分页
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectNotifications(int userId,String topic,int offset, int limit);

    /**
     * 查询某个user的某个topic的最新的一条通知
     * @param userId user的id
     * @param topic 要查询的topic
     * @return 如果不存在该topic的通知，则返回null
     */
    Message selectLatestNotificationByUserIdAndTopic(int userId,String topic);

    /**
     * 查询某个user的某个topic的所有通知的数量
     * @param userId user的id
     * @param topic 要查询的topic
     * @return
     */
    int selectNotificationsCountByUserIdAndTopic(int userId, String topic);

    /**
     * 查询某个user的某个topic的所有未读通知的数量,
     * @param userId user的id
     * @param topic 要查询的topic，如果topic为null，则查询该用户所有主题的未读消息总数
     * @return
     */
    int selectUnreadNotificationsCountByUserIdAndTopic(int userId, String topic);

    /**
     * 查询当前用户的会话列表，每个会话只显示一条最新的message(用于在界面上显示)
     * 该方法支持分页
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户有多少个会话
     */
    int selectConversationsRows(int userId);

    /**
     * 查询某个具体会话的所有Message,会话通过conversationId确定
     * 该方法支持分页
     * @param conversationId
     * @return
     */
    List<Message> selectMessagesByConversationId(String conversationId,int offset,int limit);

    /**
     * 查询某个具体会话有多少条Message
     */
    int selectMessagesRowsByConversationId(String conversationId);

    /**
     * 查询未读消息数量
     * 如果conversationId为null，则查询用户所有的未读消息数量
     * 如果conversationId不为null，则查询该用户指定会话的未读消息数量
     */
    int selectUnreadMessagesCount(int userId, String conversationId);

    /**
     * 将message对象存储到数据库中
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 更新id属于messageIds的所有的message的status字段
     */
    int updateStatusById(List<Integer> messageIds,int status);
}
