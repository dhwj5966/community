package com.starry.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.starry.community.bean.Event;
import com.starry.community.bean.Message;
import com.starry.community.mapper.MessageMapper;
import com.starry.community.service.MessageService;
import com.starry.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-18-10:26 AM
 * @Describe
 */
@Component
public class EventConsumer implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    /**
     * Kafka的消费者，消费topic为 comment,follow,like
     * 监听comment,follow,like事件，生成Message对象并存入数据库，作为对用户的系统通知。
     * @param record
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record) {
        //增强健壮性以及日志记录
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return;
        }
        //将record中的value取出并转换为Event对象
        String value = record.value().toString();
        JSONObject jsonObject = JSONObject.parseObject(value);
        Event event = jsonObject.to(Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        //
        Message message = convertEventToMessage(event);
        messageService.addMessage(message);
    }

    /**
     * 将event对象转换为Message对象
     * @param event
     * @return
     */
    private Message convertEventToMessage(Event event) {
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());

        //构建map，转化成Json字符串，存入content
        Map<String, Object> map = new HashMap<>();
        map.put("userId", event.getUserId());
        map.put("entityType", event.getEntityType());
        map.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            map.putAll(event.getData());
        }
        message.setContent(JSONObject.toJSONString(map));
        return message;
    }
}
