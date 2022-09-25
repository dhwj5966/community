package com.starry.community.service.impl;

import com.starry.community.bean.DiscussPost;
import com.starry.community.mapper.DiscussPostMapper;
import com.starry.community.service.DiscussPostService;
import com.starry.community.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-02-6:05 PM
 * @Describe
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

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
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPostsByUserId(userId, offset, limit);
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
