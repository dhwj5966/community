package com.starry.community.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Message;
import com.starry.community.bean.User;
import com.starry.community.mapper.elasticsearch.DiscussPostRepository;
import com.starry.community.service.ElasticSearchService;
import com.starry.community.service.LikeService;
import com.starry.community.util.RedisKeyUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    public void esst() {

    }

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

    }

    @Test
    public void noHighLightSearch() throws IOException {
        //构造搜索条件
        SearchRequest searchRequest = new SearchRequest("discusspost");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网","title","content"))//title或content里有互联网
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))//按type字段，倒序
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)
                .from(10);
        searchRequest.source(searchSourceBuilder);
        //执行搜索,得到响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //从响应里提取想要的数据
        List<DiscussPost> result = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);
            System.out.println(discussPost);
        }

    }

    @Test
    public void HighLightSearch() throws IOException {
        //初始化查询请求
        SearchRequest searchRequest = new SearchRequest("discusspost");
        //构造高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        //构造搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网",
                        "title","content"))//title或content里有互联网
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))//按type字段，倒序
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)
                .from(10)
                .highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        //执行搜索,得到响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //从响应里提取想要的数据
        List<DiscussPost> result = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);
            //高亮部分需要专门提取
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            result.add(discussPost);
        }
        result.forEach(System.out::println);
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
