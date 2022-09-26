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
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_LOGIN = "login";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";

    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }



    public static String getUserKey(int userId) {
        return PREFIX_USER + userId;
    }

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

    /**
     * 某个实体拥有的粉丝的key
     * follower:entityType:entityId -> zset(time userId)
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT +entityId;
    }

    /**
     * 某个用户关注的实体的key
     * followee:userId:entityType -> zset(time entityId)
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getKaptchaKey(String randomString) {
        return PREFIX_KAPTCHA + SPLIT + randomString;
    }

    public static String getLoginUserKey(String ticket) {
        return PREFIX_LOGIN + SPLIT + ticket;
    }
}
