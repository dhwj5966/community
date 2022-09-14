package com.starry.community.service.impl;

import com.starry.community.service.LikeService;
import com.starry.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author Starry
 * @create 2022-09-14-2:08 PM
 * @Describe
 */
@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void like(int entityType, int entityId, int userId, int targetUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(targetUserId);
                Boolean member = redisTemplate.opsForSet().isMember(entityLikKey, userId);
                operations.multi();
                if (member) {
                    redisTemplate.opsForSet().remove(entityLikKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                } else {
                    redisTemplate.opsForSet().add(entityLikKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }


    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public int isUserLikeEntity(int entityType, int entityId, int userId) {
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        int status = redisTemplate.opsForSet().isMember(key, userId) ? 1 : 0;
        return status;
    }

    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Object o = redisTemplate.opsForValue().get(userLikeKey);
        return o == null ? 0 : (int) o;
    }
}
