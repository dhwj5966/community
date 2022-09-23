package com.starry.community.controller;

import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Page;
import com.starry.community.service.ElasticSearchService;
import com.starry.community.service.LikeService;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-22-3:03 PM
 * @Describe
 */
@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String Search(String keyword, Page page, Model model) {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException();
        }
        page.setPath("/search?keyword=" + keyword);
        page.setLimit(5);
        Map<String, Object> searchResultMap
                = elasticSearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        if (searchResultMap == null) {
            throw new RuntimeException();
        }
        Object count = searchResultMap.get("count");
        Long count2 = (Long) count;
        page.setRows(count2.intValue());
        List<DiscussPost> discussPosts = (List<DiscussPost>) searchResultMap.get("discussPosts");
        List<Map<String, Object>> list = new LinkedList<>();
        if (discussPosts != null) {
            for (DiscussPost discussPost : discussPosts) {
                Map<String,Object> map = new HashMap<>();

                map.put("post", discussPost);
                map.put("user", userService.findUserById(discussPost.getUserId()));
                map.put("like", likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));

                list.add(map);
            }
        }

        model.addAttribute("discussposts",list);
        return "site/search";
    }
}
