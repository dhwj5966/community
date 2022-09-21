package com.starry.community.mapper;

import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Message;
import com.starry.community.bean.User;
import com.starry.community.mapper.elasticsearch.DiscussPostRepository;
import com.starry.community.service.LikeService;
import com.starry.community.util.RedisKeyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
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

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void query1() {
        PriorityQueue heap = new PriorityQueue();

    }


    @Test
    public void insertAllPost() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost();
        discussPostRepository.saveAll(discussPosts);
    }

    @Test
    public void deleteAll() {
        discussPostRepository.deleteAll();
    }

    @Test
    public void test1() {
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
        System.out.println(discussPostRepository.count());

//        Iterable<DiscussPost> all = discussPostRepository.findAll();
//        Iterator<DiscussPost> iterator = all.iterator();
//        while (iterator.hasNext()) {
//            DiscussPost next = iterator.next();
//            System.out.println(next);
//        }


//        DiscussPost discussPost = new DiscussPost();
//        discussPost.setId(3);
//        discussPostRepository.save(discussPost);
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



class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        int dp = solution.findTargetSumWays(new int[]{1,1,1,1,1},3);
        System.out.println(dp);
    }
    Map[] memory;
    int t ;
    public int findTargetSumWays(int[] nums, int target) {
        //回溯 + 剪枝
        memory = new Map[nums.length];
        Arrays.fill(memory, new HashMap<Integer,Integer>());
        this.t = target;
        return dp(0, 0, nums);
    }

    //index代表当前看的索引位置,target是需要达到的目标值
    public int dp(int index, int target, int[] nums) {
        if (index >= nums.length) {
            return target == t ? 1 : 0;
        }
        if (memory[index].containsKey(target)) {
            return (int)memory[index].get(target);
        }
        int i1 = dp(index + 1, target + nums[index], nums);
        int i2 = dp(index + 1, target - nums[index], nums);
        memory[index].put(target, i1 + i2);
        return i1 + i2;
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
