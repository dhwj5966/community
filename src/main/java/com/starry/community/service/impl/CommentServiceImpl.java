package com.starry.community.service.impl;

import com.starry.community.bean.Comment;
import com.starry.community.mapper.CommentMapper;
import com.starry.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-09-9:31 AM
 * @Describe
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectComments(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentsRows(int entityType, int entityId) {
        return commentMapper.selectCommentRows(entityType,entityId);
    }
}
