package com.starry.community.mapper;

import com.starry.community.bean.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-09-1:06 AM
 * @Describe
 */
@Mapper
public interface CommentMapper {

    /**
     * 查询指定entityType，指定entityId的评论的条数，如果entityType==0，则不限定类型，如果entityId==0，则不限定entityId
     */
    public int selectCommentRows(int entityType, int entityId);


    public List<Comment> selectComments(int entityType, int entityId, int offset, int limit);
}
