package com.starry.community.mapper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Starry
 * @create 2022-09-18-1:39 AM
 * @Describe
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTest {
    @Autowired
    KafkaProducer kafkaProducer;
    @Test
    public void testKafka() throws InterruptedException {
        kafkaProducer.sendMessage("test","你好，请问你收到了吗？hello！！！");
        Thread.sleep(100000);
    }
}

@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer {
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println("topic:" + record.topic());
        System.out.println("headers:" + record.headers());
        System.out.println("offset:" + record.offset());
        System.out.println("partition:" + record.partition());
        System.out.println("timestamp:" + record.timestamp());
        System.out.println("key:" + record.key());
        System.out.println("value:" + record.value());
    }
}
