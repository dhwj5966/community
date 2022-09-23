package com.starry.community.util;

/**
 * @author Starry
 * @create 2022-09-05-12:09 AM
 * @Describe
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FALIURE = 2;

    /**
     * 默认的登录凭证超时时间
     */
    Long DEFAULT_EXPIRED_SECONDS = 3600l * 12;

    /**
     * 记住我状态下的登录凭证超时时间
     */
    Long REMEMBER_EXPIRED_SECONDS = 3600l * 12 * 60;

    /**
     * 实体类型-帖子，在评论中代表评论的对象是帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型-评论,在评论中代表评论的对象是帖子的评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型-用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 实体类型-帖子
     */
    int ENTITY_TYPE_DISCUSSPOST = 4;

    String TOPIC_COMMENT = "comment";

    String TOPIC_LIKE = "like";

    String TOPIC_FOLLOW = "follow";

    String TOPIC_PUBLISH = "publish";

    int SYSTEM_USER_ID = 1;
}
