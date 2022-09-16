package com.starry.community.service.impl;

import com.starry.community.bean.User;
import com.starry.community.service.FollowService;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Starry
 * @create 2022-09-15-9:22 AM
 * @Describe
 */
@Service
public class FollowServiceImpl implements FollowService, CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public List<Map<String, Object>> getFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (ids == null) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>(ids.size());
        for (int id : ids) {
            Map<String,Object> map = new HashMap<>(2);
            User user = userService.findUserById(id);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime", new Date(score.longValue()));
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> userIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (userIds == null) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>(userIds.size());
        for (int id : userIds) {
            Map<String,Object> map = new HashMap<>(2);
            User user = userService.findUserById(id);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime", new Date(score.longValue()));
            result.add(map);
        }
        return result;
    }

    @Override
    public boolean getFollowStatus(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    @Override
    public long getFollowersCount(int userId) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    @Override
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                redisTemplate.multi();
                redisTemplate.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                return redisTemplate.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                redisTemplate.multi();
                redisTemplate.opsForZSet().remove(followerKey,userId);
                redisTemplate.opsForZSet().remove(followeeKey,entityId);
                return redisTemplate.exec();
            }
        });
    }
}
