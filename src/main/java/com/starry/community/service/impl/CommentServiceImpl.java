package com.starry.community.service.impl;

import com.starry.community.bean.Comment;
import com.starry.community.mapper.CommentMapper;
import com.starry.community.service.CommentService;
import com.starry.community.service.DiscussPostService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-09-9:31 AM
 * @Describe
 */
@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;
    @Override
    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectComments(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentsRows(int entityType, int entityId) {
        return commentMapper.selectCommentRows(entityType,entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("不能插入空评论");
        }
        //敏感词过滤,html标签转义
        comment.setContent(sensitiveWordsFilter.filter(HtmlUtils.htmlEscape(comment.getContent())));
        int rows = commentMapper.insertComment(comment);
        //插入之后，判断一下comment的entityType,如果是帖子的评论，就把帖子的评论数量增加
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCommentRows(ENTITY_TYPE_POST, comment.getEntityId());
            discussPostService.updateCommentCountById(comment.getEntityId(), count);
        }
        return rows;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
