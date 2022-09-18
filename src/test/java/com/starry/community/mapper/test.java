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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        Object o = null;
        User u = (User) o;
        System.out.println(u);
    }

    @Test
    public void test2() {
        long l = System.currentTimeMillis();
        Map<Integer,Integer> map = new HashMap<>();
        for (int j = 0; j < 16; j++) {
            map.put(j,0);
        }
        System.out.println(System.currentTimeMillis() - l);
    }


    @Test
    public void test3() {
        redisTemplate.opsForValue().set("gg",null);
    }

}

class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        boolean palindrome = solution.isPalindrome("0P");

    }
    public boolean isPalindrome(String s) {
        //双指针，时间复杂度O(n),空间复杂度O(1)
        char[] chars = s.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        while (left < right) {
            while (left < right && !isValid(chars[left])) {
                left++;
            }
            while (left < right && !isValid(chars[right])) {
                right--;
            }
            if (left < right) {
                if (equal(chars[left], chars[right])) {
                    left++;
                    right--;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    //验证字符是否是字母或者数字
    public boolean isValid(char c) {
        return (c >= '0' && c <= '9') || (c >='a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    //判断a和b是否满足回文
    public boolean equal(char a, char b) {
        return a == b || a == b - ('A' - 'a') || a == b - ('a' - 'A');
    }
}
