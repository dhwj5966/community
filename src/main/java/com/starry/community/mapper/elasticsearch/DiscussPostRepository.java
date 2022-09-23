package com.starry.community.mapper.elasticsearch;

import com.starry.community.bean.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Starry
 * @create 2022-09-21-3:30 PM
 * @Describe
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
    long countByCommentCount(int commentCount);

    List<DiscussPost> findDiscussPostByCommentCount(int commentCount);

    void deleteById(int id);
}


