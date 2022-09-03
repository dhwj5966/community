package com.starry.community.service;

import com.starry.community.bean.DiscussPost;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-02-6:05 PM
 * @Describe discuss_post的Service层
 */
public interface DiscussPostService {
    /**
     * 根据userId查询DiscussPost，如果userId为0，则查询全部userId的DiscussPost，不包括status=2的DiscussPost
     * @param userId
     * @param offset 从查询结果的offset条开始
     * @param limit 记录数
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    /**
     * 根据userId查询DiscussPost数，如果userId为0，则查询全部userId的DiscussPost数，不包括status=2的DiscussPost
     * @param userId
     * @return 记录数
     */
    int findDiscussPostsPostRows(int userId);
}
