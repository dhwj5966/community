package com.starry.community.service;

import java.util.List;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-15-9:04 AM
 * @Describe 关注模块的业务层
 */
public interface FollowService {
    //想想应该有哪些方法呢？
    //获取粉丝
//    List<Integer> getFollower(int entityType, int entityId);
//

    /**
     * 查询用户是否已关注实体，如果已经关注了则返回true，否则false
     */
    boolean getFollowStatus(int userId,int entityType,int entityId);
//
//    //获取指定用户对指定实体类型的所有关注
//    List<Integer> getFollowee(int entityType,int userId);

    /**
     * 查询指定用户的粉丝(用户)数量
     */
    long getFollowersCount(int userId);

    /**
     * 统计指定用户关注的实体的数量
     */
    long getFolloweeCount(int userId,int entityType);

    /**
     * 获取指定用户的所有粉丝，支持分页
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String,Object>> getFollowers(int userId, int offset, int limit);

    /**
     * 查询用户关注的所有用户
     * @return
     */
    List<Map<String,Object>> getFollowees(int userId,int offset,int limit);

    /**
     * 用户关注实体
     * @param userId
     * @param entityType
     * @param entityId
     */
    void follow(int userId, int entityType,int entityId);

    /**
     * 用户取关实体
     * @param userId
     * @param entityType
     * @param entityId
     */
    void unfollow(int userId, int entityType,int entityId);
}
