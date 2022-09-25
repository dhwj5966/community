package com.starry.community.service;

import com.starry.community.bean.DiscussPost;

import java.util.List;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-22-1:20 PM
 * @Describe
 */
public interface ElasticSearchService {


    void saveDiscussPost(DiscussPost discussPost);


    void deleteDiscussPostById(int id);

    /**
     * 根据关键词检索帖子，支持分页，
     * 检索字段包括title、content，
     * 对于搜索到的discussPost，其title和content字段中的keyword会高亮显示,
     * 搜索的排序逻辑：首先按type倒序、分数倒序、发布日期倒序。
     * @param keyword 关键词
     * @param current 起始页数，从0开始
     * @param size 每页包含的数据条数
     * @return
     */
    Map<String, Object> searchDiscussPost(String keyword, int current, int size);

}
