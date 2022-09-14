package com.starry.community.controller;

import com.starry.community.bean.Comment;
import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Page;
import com.starry.community.bean.User;
import com.starry.community.service.CommentService;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.LikeService;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Starry
 * @create 2022-09-07-5:25 PM
 * @Describe
 */
@Controller
@RequestMapping("/discussPost")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    /**
     * 跳转到帖子详情页
     * 根据帖子ID查到DiscussPost对象，并封装到Model里
     * 还要查到所有的评论信息
     * @return
     */
    @RequestMapping(value = "/showPostDetail/{postId}", method = RequestMethod.GET)
    public String showPostDeatil(@PathVariable("postId")int postId, Model model, Page page) {
        User curUser = hostHolder.getUser();
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        if (discussPost == null) {
            throw new RuntimeException();
        }
        User user = userService.findUserById(discussPost.getUserId());
        if (user == null) {
            throw new RuntimeException();
        }
        if (page == null) {
            throw new RuntimeException();
        }
        page.setLimit(5);
        page.setPath("/discussPost/showPostDetail/" + postId);
        page.setRows(discussPost.getCommentCount());
        List<Comment> comments = commentService.findComments(ENTITY_TYPE_POST,
                discussPost.getId(),page.getOffset(),page.getLimit());
        for (Comment comment : comments) {
            comment.setLike(curUser == null ? 0 :
                    likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId()));
            comment.setLikeStatus(
                    likeService.isUserLikeEntity(ENTITY_TYPE_COMMENT,comment.getId(), curUser.getId()));
            if (comment.getComments() != null) {
                for (Comment reply : comment.getComments()) {
                    reply.setLike(likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));
                    reply.setLikeStatus(curUser == null ? 0 :
                            likeService.isUserLikeEntity(ENTITY_TYPE_COMMENT, reply.getId(), curUser.getId()));
                }
            }
        }
        model.addAttribute("comments", comments);
        model.addAttribute("post", discussPost);
        model.addAttribute("user", user);
        model.addAttribute("like", likeService.findEntityLikeCount(1, postId));
        int likeStatus = curUser == null ? 0 : likeService.isUserLikeEntity(ENTITY_TYPE_POST,postId, curUser.getId());
        model.addAttribute("likeStatus",likeStatus);
        return "/site/discuss-detail";
    }




    /**
     * 发布post
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
