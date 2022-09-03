package com.starry.community.service.impl;

import com.starry.community.bean.DiscussPost;
import com.starry.community.mapper.DiscussPostMapper;
import com.starry.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPostsByUserId(userId, offset, limit);
    }

    @Override
    public int findDiscussPostsPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
