package com.starry.community.controller;

import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.Comment;
import com.starry.community.bean.User;
import com.starry.community.service.CommentService;
import com.starry.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-10-1:25 PM
 * @Describe
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    /**
     *
     * @param comment 包括entityType，entityId，content，可能有targetUser（如果是对评论的回复）
     */
    @RequestMapping(value = "/add/{discussPostId}",method = RequestMethod.POST)
    @CheckLogin
    public String addComment(Comment comment,@PathVariable("discussPostId") int discussPostId) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date());
        int i = commentService.addComment(comment);
        return "redirect:/discussPost/showPostDetail/" + discussPostId;
    }
}
