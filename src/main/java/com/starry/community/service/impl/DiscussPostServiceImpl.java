package com.starry.community.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.starry.community.bean.DiscussPost;
import com.starry.community.mapper.DiscussPostMapper;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.ElasticSearchService;
import com.starry.community.service.LikeService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.RedisKeyUtil;
import com.starry.community.util.SensitiveWordsFilter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Starry
 * @create 2022-09-02-6:05 PM
 * @Describe
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Value(value = "${caffeine.posts.maxSize}")
    private int maxSize;

    @Value(value = "${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //?????????????????????
    private LoadingCache<Integer, Integer> postRowsCache;
    //?????????????????????
    private LoadingCache<String, List<DiscussPost>> postListCache;

    private static final Date EPOCH_STARRY;

    @PostConstruct//?????????????????????
    public void init() {
        //?????????postListCache
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize) //???????????????
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)//?????????????????????????????????
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    //??????????????????????????????????????????
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if (StringUtils.isBlank(key)) {
                            throw new IllegalArgumentException("????????????");
                        }
                        String[] split = key.split(":");
                        if (split == null || split.length != 2) {
                            throw new IllegalArgumentException("????????????");
                        }
                        int offset = Integer.parseInt(split[0]);
                        int limit = Integer.parseInt(split[1]);

                        return discussPostMapper.selectDiscussPostsByUserId(0, offset, limit, 1);
                    }
                });
        //?????????postRowsCache
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(1l)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer key) throws Exception {
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    @Override
    public int findDiscussPostsPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        logger.debug("??????DB?????????????????????");
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }

        logger.debug("??????DB???????????????");
        return discussPostMapper.selectDiscussPostsByUserId(userId, offset, limit, orderMode);
    }

    static {
        try {
            EPOCH_STARRY = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-09-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateScore(int discussPostId) {
        DiscussPost post = findDiscussPostById(discussPostId);
        if (post == null) {
            return;
        }
        //????????????
        double score = getScore(post);
        //?????????????????????
        updateScore(discussPostId, score);
        //??????ES?????????
        post.setScore(score);
        elasticSearchService.saveDiscussPost(post);
    }

    private void updateScore(int discussPostId, double score) {
        discussPostMapper.updateScoreById(discussPostId, score);
    }

    /**
     * ?????????????????????
     * @param discussPost
     * @return
     */
    private double getScore(DiscussPost discussPost) {
        if (discussPost == null) {
            return 0.0;
        }
        //??????
        //????????????
        int isWonderful = discussPost.getStatus() == 1 ? 10 : 0;
        int commentCount = discussPost.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        //??????
        double w = isWonderful + commentCount * 10 + likeCount * 2;

        long day = (discussPost.getCreateTime().getTime() - EPOCH_STARRY.getTime()) / (3600 * 24 * 1000);

        return Math.log10(Math.max(w, 1)) + day;
    }

    @Override
    public void waitToUpdateScore(int discussPostId) {
        String key = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(key, discussPostId);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateTypeById(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatusById(id, status);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }



    @Override
    public int addDiscussPosts(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("??????????????????");
        }
        //?????????????????????????????????
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(sensitiveWordsFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveWordsFilter.filter(discussPost.getContent()));
        //???????????????
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public int updateCommentCountById(int id, int commentCount) {
        return discussPostMapper.updateCommentCountById(commentCount,id);
    }
}
