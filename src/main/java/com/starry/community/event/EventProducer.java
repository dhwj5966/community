package com.starry.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.starry.community.bean.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Starry
 * @create 2022-09-18-10:23 AM
 * @Describe 事件的生产者
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 接收事件对象，将事件对象event转换为Json字符串，并存入Kafka的topic中
     * @param event
     */
    public void fireEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException();
        }
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}

