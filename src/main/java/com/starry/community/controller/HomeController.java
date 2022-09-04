package com.starry.community.controller;

import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Page;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-02-6:57 PM
 * @Describe
 */
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    /**
     *
     * @param model
     * @param page 客户端要传入page对象的current和limit属性
     * @return
     */
    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page) {
        //
        page.setRows(discussPostService.findDiscussPostsPostRows(0));//查询总数据数量，并封装到page对象中
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",discussPost);
                map.put("user",userService.findUserById(discussPost.getUserId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("page",page);
        return "index";
    }
}
