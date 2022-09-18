package com.starry.community.mapper;

import com.starry.community.bean.Message;
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
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void test1() {
        Message message = messageMapper.selectLatestNotificationByUserIdAndTopic(154, "comment");
        System.out.println(message);
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
        HashMap<Object, Object> map = new HashMap<>();
        System.out.println(map.get("aa"));
    }

}

class MapSum {
    public static void main(String[] args) {

    }
    /**
     用字典树
     */
    private TreeNode root;
    /** Initialize your data structure here. */
    public MapSum() {
        root = new TreeNode(0);
    }

    public void insert(String key, int val) {
        TreeNode temp = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            TreeNode next = temp.children[c - 'a'];
            if (next == null) {
                next = new TreeNode(val);
                temp.children[c - 'a'] = next;
            } else {
                next.count += val;
            }
            temp = next;
        }
        //如果该节点是一个尾节点,且之前的val不等于现在的val,则再次遍历以更新count
        if (temp.isEnd && temp.endVal != val) {
            int change = temp.endVal;
            temp = root;
            for (int i = 0; i < key.length(); i++) {
                char c = key.charAt(i);
                TreeNode next = temp.children[c - 'a'];
                next.count -= change;
                temp = next;
            }
        } else {
            temp.isEnd = true;
            temp.endVal = val;
        }
    }





    public int sum(String prefix) {
        TreeNode temp = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            TreeNode next = temp.children[c - 'a'];
            if (next == null) {
                return 0;
            }
            temp = next;
        }
        return temp.count;
    }

    private class TreeNode {
        //记录目前key的值的总和
        int count;
        TreeNode[] children;
        boolean isEnd;
        int endVal;

        TreeNode(int count) {
            this.count = count;
            children = new TreeNode[26];
        }
    }
}
