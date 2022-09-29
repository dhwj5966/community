package com.starry.community.controller;

import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Page;
import com.starry.community.mapper.MessageMapper;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.LikeService;
import com.starry.community.service.MessageService;
import com.starry.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    private LikeService likeService;

    /**
     *
     * @param model
     * @param page 客户端要传入page对象的current和limit属性
     * @return
     */
    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        //
        page.setRows(discussPostService.findDiscussPostsPostRows(0));//查询总数据数量，并封装到page对象中
        page.setPath("/index?orderMode=" + orderMode);
        if (page.getCurrent() > page.getTotal()) {
            page.setCurrent(page.getTotal());
        }
        List<DiscussPost> list =
                discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",discussPost);
                map.put("user",userService.findUserById(discussPost.getUserId()));
                map.put("like", likeService.findEntityLikeCount(1, discussPost.getId()));
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("page",page);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    @RequestMapping(value = "/error",method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
