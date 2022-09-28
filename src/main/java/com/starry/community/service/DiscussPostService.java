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
     * 重新计算指定id的discusspost的score,不仅要更新数据库，还要更新ES(将事件提交给MQ)
     * @param discussPostId
     */
    void updateScore(int discussPostId);


    /**
     * 把待更新分数的帖子id存入到redis的Set中，等待定时任务的执行，将所有待更新分数的帖子的Score更新一遍。
     * @param discussPostId
     */
    void waitToUpdateScore(int discussPostId);


    /**
     * 修改指定id的discusspost的type。
     * @param id
     * @param type
     * @return
     */
    int updateType(int id, int type);

    /**
     * 修改指定id的discusspost的status
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    DiscussPost findDiscussPostById(int id);
    /**
     * 根据userId查询DiscussPost，如果userId为0，则查询全部userId的DiscussPost，不包括status=2的DiscussPost
     * @param userId
     * @param offset 从查询结果的offset条开始
     * @param limit 记录数
     * @param orderMode 排序模式，除置顶帖子外，orderMode == 0则按时间倒序,orderMode == 1则按score倒序。
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * 根据userId查询DiscussPost数，如果userId为0，则查询全部userId的DiscussPost数，不包括status=2的DiscussPost
     * @param userId
     * @return 记录数
     */
    int findDiscussPostsPostRows(int userId);

    /**
     * 传进来的帖子只有title和content两个属性
     * @param discussPost
     * @return
     */
    int addDiscussPosts(DiscussPost discussPost);

    /**
     * 更新指定id的discussPost的commentCount
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCountById(int id,int commentCount);
}
