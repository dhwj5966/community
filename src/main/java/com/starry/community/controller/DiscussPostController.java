package com.starry.community.controller;

import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.User;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-07-5:25 PM
 * @Describe
 */
@Controller
@RequestMapping("/discussPost")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    /**
     * 根据帖子ID查到DiscussPost对象，并封装到Model里
     * @return
     */
    @RequestMapping(value = "/showPostDetail/{postId}", method = RequestMethod.GET)
    public String showPostDeatil(@PathVariable("postId")int postId, Model model) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        if (discussPost == null) {
            throw new RuntimeException();
        }
        User user = userService.findUserById(discussPost.getUserId());
        if (user == null) {
            throw new RuntimeException();
        }
        model.addAttribute("post",discussPost);
        model.addAttribute("user",user);
        return "/site/discuss-detail";
    }




    /**
     * 如果用户未登录，发帖失败
     * 如果用户登录成功
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(403, "请先登录！");
        }
        if (StringUtils.isBlank(title)) {
            return CommunityUtil.getJsonString(403,"标题不能为空！");
        }
        if (StringUtils.isBlank(content)) {
            return CommunityUtil.getJsonString(403,"内容不能为空");
        }

        //封装帖子
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setUserId(user.getId());
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPosts(discussPost);

        //返回结果
        return CommunityUtil.getJsonString(0,"发布成功！");
    }
}
