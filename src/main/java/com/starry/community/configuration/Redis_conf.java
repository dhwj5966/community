package com.starry.community.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Starry
 * @create 2022-09-14-8:55 AM
 * @Describe
 */
@Configuration
public class Redis_conf {


    @Bean
    @Primary
    public RedisTemplate<String,Object> RedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer redisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //设置key的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //设置value的序列化方式
        template.setValueSerializer(redisSerializer);
        //设置Hash key的序列化
        template.setHashKeySerializer(stringRedisSerializer);
        //设置Hash value的序列化方式
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
