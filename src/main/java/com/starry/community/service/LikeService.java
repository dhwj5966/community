package com.starry.community.service;

/**
 * @author Starry
 * @create 2022-09-14-2:04 PM
 * @Describe 点赞的业务实现
 */
public interface LikeService {

    /**
     * 如果用户已经点过赞了，取消赞，如果用户没有点过赞，点赞。
     * 并且不管是取消赞还是点赞，都要更新被点赞(取消赞)的计数器
     * key(get from RedisKeyUtil) -> set[userid...]
     * @param entityType
     * @param entityId
     * @param userId 点赞者id
     * @param targetUserId 被点赞者id
     */
    void like(int entityType, int entityId, int userId, int targetUserId);


    /**
     * 查询实体的点赞数量，通过entityType和entityId来定位实体
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 查询指定用户与实体的点赞状态
     * @return 1代表点赞了，0代表未点赞
     */
    int isUserLikeEntity(int entityType, int entityId, int userId);

    /**
     * 根据userId查询用户被点赞数
     * @param userId
     * @return
     */
    int findUserLikeCount(int userId);
}
