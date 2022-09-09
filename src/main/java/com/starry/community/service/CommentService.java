package com.starry.community.service;

import com.starry.community.bean.Comment;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-09-9:30 AM
 * @Describe
 */
public interface CommentService {
    /**
     * 根据指定条件查询评论
     * @param entityType 评论的类型
     * @param entityId 评论所属实体的id，比如Type为帖子的评论，entityId就指向帖子的id，如果Type为评论的评论，entityId就指向评论的id
     * @param offset 偏移量，用来分页
     * @param limit 每页所需数据
     * @return
     */
    List<Comment> findComments(int entityType, int entityId, int offset, int limit);

    /**
     * 根据entityType和entityId查询评论数
     */
    int findCommentsRows(int entityType, int entityId);
}
