package com.starry.community.service.impl;

import com.starry.community.bean.DiscussPost;
import com.starry.community.mapper.DiscussPostMapper;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.ElasticSearchService;
import com.starry.community.service.LikeService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.RedisKeyUtil;
import com.starry.community.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Starry
 * @create 2022-09-02-6:05 PM
 * @Describe
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService, CommunityConstant {
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

    private static final Date EPOCH_STARRY;

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
        //计算分数
        double score = getScore(post);
        //更新数据库数据
        updateScore(discussPostId, score);
        //更新ES的数据
        post.setScore(score);
        elasticSearchService.saveDiscussPost(post);
    }

    private void updateScore(int discussPostId, double score) {
        discussPostMapper.updateScoreById(discussPostId, score);
    }

    /**
     * 计算帖子的得分
     * @param discussPost
     * @return
     */
    private double getScore(DiscussPost discussPost) {
        if (discussPost == null) {
            return 0.0;
        }
        //公式
        //是否加精
        int isWonderful = discussPost.getStatus() == 1 ? 10 : 0;
        int commentCount = discussPost.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        //权重
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
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        return discussPostMapper.selectDiscussPostsByUserId(userId, offset, limit, orderMode);
    }

    @Override
    public int findDiscussPostsPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPosts(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("帖子不能为空");
        }
        //标签转义，并过滤敏感词
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(sensitiveWordsFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveWordsFilter.filter(discussPost.getContent()));
        //存入数据库
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public int updateCommentCountById(int id, int commentCount) {
        return discussPostMapper.updateCommentCountById(commentCount,id);
    }
}
