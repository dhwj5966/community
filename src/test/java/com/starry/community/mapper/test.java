package com.starry.community.mapper;

import com.starry.community.mapper.elasticsearch.DiscussPostRepository;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.ElasticSearchService;
import com.starry.community.service.LikeService;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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


    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private Scheduler scheduler;

    @Test
    public void test1() throws InterruptedException, SchedulerException {

    }

//    @Test
//    public void test99() throws InterruptedException {//184 1204
//        List<DiscussPost> discussPosts =
//                discussPostService.findDiscussPosts(0, 0, 10, 1);
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
//            List<DiscussPost> discussPosts1 =
//                    discussPostService.findDiscussPosts(0, 0, 10, 1);
//        }
//        System.out.println(System.currentTimeMillis() - start);
//    }
//
//    @Test
//    public void esst() {
//        LocalDate start = LocalDate.of(2022,9, 24);
//        LocalDate end = LocalDate.of(2022,9, 26);
//        Map<String, Long> map = new TreeMap<>();
//        LocalDate temp = LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth());
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy:MM:dd");
//        Long sum = 0l;
//        while (!temp.isAfter(end)) {
//            String key = RedisKeyUtil.getDAUKey(dateTimeFormatter.format(temp));
//            Long size = (Long) redisTemplate.execute(new RedisCallback() {
//                @Override
//                public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                    return connection.bitCount(key.getBytes());
//                }
//            });
//            map.put(dateTimeFormatter2.format(temp), size);
//            temp = temp.plusDays(1);
//            sum += size;
//        }
//        map.put("sum", sum);
//        System.out.println(map);
//    }
//
//    @Test
//    public void query1() {
//        //解析日期
//        System.out.println(redisTemplate.getExpire("login:0682afd750cc4ac3aa0e28272e834f77"));
//    }
//
//
//    //场景模拟，统计20万个重复数据的独立总数
//    @Test
//    public void HyperLogLog() {
//        String key = "hyper1";
//        for (int i = 0; i < 10_0000; i++) {
//            redisTemplate.opsForHyperLogLog().add(key, i);
//        }
//
//        for (int i = 0; i < 10_0000; i++) {
//            redisTemplate.opsForHyperLogLog().add(key, (int)(Math.random() * 10_0000));
//        }
//    }
//
//    @Test
//    public void BitmapTest() {
//        String key1 = "bit1";
//        String key2 = "bit2";
//
//        redisTemplate.opsForValue().setBit(key1, 0,true);
//        redisTemplate.opsForValue().setBit(key1, 1,true);
//        redisTemplate.opsForValue().setBit(key1, 2,true);
//
//        redisTemplate.opsForValue().setBit(key2, 2,true);
//        redisTemplate.opsForValue().setBit(key2, 3,true);
//        redisTemplate.opsForValue().setBit(key2, 4,true);
//
//        String key3 = "bitAnd";
//
//        Object execute = redisTemplate.execute(new RedisCallback() {
//            @Override
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                connection.bitOp(RedisStringCommands.BitOperation.AND,
//                        key3.getBytes(), key1.getBytes(),key2.getBytes());
//                return null;
//            }
//        });
//
//    }
//
//
//    @Test
//    public void insertAllPost() {
//        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost();
//        discussPostRepository.saveAll(discussPosts);
//    }
//
//    @Test
//    public void deleteAll() {
//        discussPostRepository.deleteAll();
//    }
//
//
//    @Test
//    public void noHighLightSearch() throws IOException {
//        //构造搜索条件
//        SearchRequest searchRequest = new SearchRequest("discusspost");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.multiMatchQuery("互联网","title","content"))//title或content里有互联网
//                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))//按type字段，倒序
//                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
//                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
//                .from(0)
//                .from(10);
//        searchRequest.source(searchSourceBuilder);
//        //执行搜索,得到响应
//        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        //从响应里提取想要的数据
//        List<DiscussPost> result = new LinkedList<>();
//        for (SearchHit hit : searchResponse.getHits().getHits()) {
//            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);
//            System.out.println(discussPost);
//        }
//
//    }
//
//    @Test
//    public void HighLightSearch() throws IOException {
//        //初始化查询请求
//        SearchRequest searchRequest = new SearchRequest("discusspost");
//        //构造高亮条件
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.field("title");
//        highlightBuilder.field("content");
//        highlightBuilder.requireFieldMatch(false);
//        highlightBuilder.preTags("<span style='color:red'>");
//        highlightBuilder.postTags("</span>");
//
//        //构造搜索条件
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.multiMatchQuery("互联网",
//                        "title","content"))//title或content里有互联网
//                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))//按type字段，倒序
//                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
//                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
//                .from(0)
//                .from(10)
//                .highlighter(highlightBuilder);
//
//        searchRequest.source(searchSourceBuilder);
//        //执行搜索,得到响应
//        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        //从响应里提取想要的数据
//        List<DiscussPost> result = new LinkedList<>();
//        for (SearchHit hit : searchResponse.getHits().getHits()) {
//            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);
//            //高亮部分需要专门提取
//            HighlightField titleField = hit.getHighlightFields().get("title");
//            if (titleField != null) {
//                discussPost.setTitle(titleField.getFragments()[0].toString());
//            }
//            HighlightField contentField = hit.getHighlightFields().get("content");
//            if (contentField != null) {
//                discussPost.setContent(contentField.getFragments()[0].toString());
//            }
//            result.add(discussPost);
//        }
//        result.forEach(System.out::println);
//    }
//
//    @Test
//    public void test2() {
//        long l = System.currentTimeMillis();
//        Map<Integer,Integer> map = new HashMap<>();
//        for (int j = 0; j < 16; j++) {
//            map.put(j,0);
//        }
//        System.out.println(System.currentTimeMillis() - l);
//    }
//
//
//    @Test
//    public void test3() {
//        elasticSearchService.deleteDiscussPostById(122);
//    }


}

