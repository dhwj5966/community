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
     * 根据指定条件查询评论，如果entityType=0，则查询所有评论，
     * 如果entityType=1则查询帖子的评论(自动查询评论的前三条评论，封装到comments字段中，并且会把评论的数量封装到commentCount字段),
     * 如果entityType=2则查询帖子的评论的评论，
     * 会根据user_id，将user对象封装到comment对象中。
     * @param entityType 评论的类型
     * @param entityId 评论所属实体的id
     * @param offset 偏移量，用来分页
     * @param limit 查询到的数据条数
     * @return 查询结果
     */
    List<Comment> findComments(int entityType, int entityId, int offset, int limit);

    /**
     * 根据entityType和entityId查询评论数
     */
    int findCommentsRows(int entityType, int entityId);

    /**
     * 将传入的comment加入数据库
     * 需要对评论的内容进行敏感词过滤，以及html标签的转义
     * 如果entityType为1，需要更新对应discussPost的commentCount需要更新
     * @param comment
     * @return
     */
    int addComment(Comment comment);
}
