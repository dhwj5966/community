package com.starry.community.mapper;

import com.starry.community.bean.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-09-1:06 AM
 * @Describe
 */
@Mapper
public interface CommentMapper {

    /**
     * 查询指定entityType，指定entityId的status=0的评论的条数。
     * 如果entityType==0，查询所有数据，
     * 如果entityType！=0 且 entityId==0，则查询所有entity_type = entityType的数据
     */
    int selectCommentRows(int entityType, int entityId);


    /**
     *  查询指定entityType，entityId的status=0的评论，
     *  如果entityType==0，查询所有数据。
     *  如果entityType！=0 且 entityId==0，则查询所有entity_type = entityType的数据。
     *  如果entityType==1，则是查找帖子的评论，那么会通过分步查询，查询该帖子评论的评论，封装到Comment对象的comments字段中。
     *  无论entityType为多少，都要根据user_id查到user，封装到Comment的user字段中
     *  @param offset offset是limit的起点，
     *  @param limit limit是查询的数量，
     *  @return 查到的结果按日期倒序封装到List中。
     */
    List<Comment> selectComments(int entityType, int entityId, int offset, int limit);

    /**
     * 插入评论到数据库
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 根据Comment的id，查询Comment对象，如果不存在则返回null
     * @param id
     * @return
     */
    Comment selectCommentById(int id);
}
