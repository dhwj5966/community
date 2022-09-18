package com.starry.community.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-18-10:06 AM
 * @Describe 事件的对象
 */
public class Event {

    /**
     * 事件所属的主题
     */
    private String topic;

    /**
     * 引起事件的user的id
     */
    private String userId;

    /**
     * 事件针对的(点赞、评论、关注)的实体类型
     */
    private int entityType;

    /**
     * 事件针对的实体id
     */
    private int entityId;

    /**
     * 事件针对的实体的所属user的id
     */
    private int entityUserId;

    /**
     * 可能需要的扩展信息
     */
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Event setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
