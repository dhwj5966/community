package com.starry.community.mapper;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import com.starry.community.mapper.elasticsearch.DiscussPostRepository;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.ElasticSearchService;
import com.starry.community.service.LikeService;
import com.starry.community.util.MessageSender;
import darabonba.core.client.ClientOverrideConfiguration;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    @Autowired
    private MessageSender messageSender;

    @Test
    public void test1() throws InterruptedException, SchedulerException, ExecutionException {

    }




}

