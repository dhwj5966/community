package com.starry.community.mapper;

import com.starry.community.bean.User;
import com.starry.community.service.LikeService;
import com.starry.community.util.RedisKeyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * @author Starry
 * @create 2022-09-14-9:00 AM
 * @Describe
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class test {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LikeService likeService;
    @Test
    public void test1() {
        //绑定
        Long l = null;
        long ll = l;
        System.out.println(ll);
    }

    @Test
    public void test2() {
        System.out.println(likeService.findUserLikeCount(154));
    }


    @Test
    public void test3() {
        long l = System.currentTimeMillis();//12735
        for (int i = 0; i < 10000; i++) {
            final int j = i;
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.opsForSet().add("testSet1",j);
                    operations.opsForSet().add("testSet2",j);
                    operations.opsForSet().add("testSet3",j);
                    return null;
                }
            });
        }
        System.out.println(System.currentTimeMillis() - l);
    }

}

class Solution {
    public static void main(String[] args) {

    }
    public int mySqrt(int x) {
        //二分查找，时间复杂度O(logx)
        long left = 0;
        long right = x;
        while (left < right) {
            long mid = left + (right - left) + 1 / 2;
            if (mid * mid == x) {
                return (int)mid;
            } else if (mid * mid < x) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return (int)left;

    }
}
