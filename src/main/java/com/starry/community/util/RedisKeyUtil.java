package com.starry.community.util;

/**
 * @author Starry
 * @create 2022-09-14-2:00 PM
 * @Describe 根据需求，快速获取Redis中的key
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 根据entityType和entityId，获取redis中的key
     * 如entityType = 3，entityId = 9 -> return like:entity:3:9
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 根据用户id，获取存储指定用户赞数的Entry的Key
     * 如userId = 2 -> return like:user:2
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

}
