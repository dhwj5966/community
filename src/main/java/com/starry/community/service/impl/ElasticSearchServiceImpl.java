package com.starry.community.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.starry.community.bean.DiscussPost;
import com.starry.community.mapper.elasticsearch.DiscussPostRepository;
import com.starry.community.service.ElasticSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-22-1:28 PM
 * @Describe
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    @Override
    public void deleteDiscussPostById(int id) {
        discussPostRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> searchDiscussPost(String keyword, int current, int size) {
        //初始化查询请求
        SearchRequest searchRequest = new SearchRequest("discusspost");
        //构造高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        //构造搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword,
                        "title","content"))//title或content里有互联网
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))//按type字段，倒序
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(current)
                .size(size)
                .highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        //执行搜索,得到响应
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //从响应里提取想要的数据
        List<DiscussPost> result = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);
            //高亮部分需要专门提取
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            result.add(discussPost);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("discussPosts",result);
        map.put("count",searchResponse.getHits().getTotalHits().value);
        return map;
    }
}
