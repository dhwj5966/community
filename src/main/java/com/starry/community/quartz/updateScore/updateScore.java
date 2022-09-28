package com.starry.community.quartz.updateScore;

import com.starry.community.service.DiscussPostService;
import com.starry.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-27-2:08 PM
 * @Describe 更新帖子的score
 */
@Component
public class updateScore implements Job {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;

    private static final Logger logger = LoggerFactory.getLogger(updateScore.class);

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据Redis中所有待更新的帖子id，逐个更新帖子分数，不仅要更新数据库的discuss_post表，还要更新ES的discusspost索引。
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("当前时间:" + simpleDateFormat.format(new Date()));
        logger.info("刷新帖子分数任务开始！");
        String key = RedisKeyUtil.getPostScoreKey();
        Integer postId = null;
        long count = 0;
        while ((postId = (Integer) redisTemplate.opsForSet().pop(key)) != null) {
            discussPostService.updateScore(postId);
            count++;
        }
        logger.info("已成功刷新帖子分数！共刷新了" + count + "个帖子的分数！");
    }
}
