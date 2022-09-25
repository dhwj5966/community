package com.starry.community.mapper;

import com.starry.community.bean.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-01-7:55 PM
 * @Describe
 */
@Mapper
public interface DiscussPostMapper {


    /**
     * 将指定帖子的status字段修改。
     * @param id
     * @param status
     * @return 影响的帖子数量
     */
    int updateStatusById(int id, int status);


    /**
     * 将指定帖子的type字段修改。
     * @param id
     * @param type
     * @return 影响帖子的数量
     */
    int updateTypeById(int id, int type);


    /**
     * 查询所有的帖子
     * @return
     */
    List<DiscussPost> selectDiscussPost();

    /**
     * 插入新post到数据库的discusspost表
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     *
     * @param userId 用来查询的userId，如果为0则代表查询全部
     * @param offset 查询结果的第多少条开始
     * @param limit 查询的记录数
     * @return
     */
    List<DiscussPost> selectDiscussPostsByUserId(int userId, int offset, int limit);

    /**
     * 根据userId，查询总记录数，如果userId等于0，则返回所有的记录数
      * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);


    DiscussPost selectDiscussPostById(int id);

    int updateCommentCountById(int commentCount,int id);
}
