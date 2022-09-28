package com.starry.community.controller;

import com.starry.community.bean.*;
import com.starry.community.event.EventProducer;
import com.starry.community.service.*;
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
    @Autowired
    private EventProducer eventProducer;

    /**
     * 处理对帖子的置顶，处理异步请求，需要"admin"权限，由SpringSecurity控制。
     * @param id
     * @return
     */
    @RequestMapping(value = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        //更新数据库
        discussPostService.updateType(id,1);
        //由于ES中也保存了帖子数据，应该需要异步的把更新提交到MQ
        Event event = new Event()
                .setEntityId(id)
                .setEntityType(ENTITY_TYPE_POST)
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId());
        eventProducer.fireEvent(event);
        //返回
        return CommunityUtil.getJsonString(0);
    }

    /**
     * 加精帖子，处理异步请求，需要"admin"权限，由SpringSecurity控制。
     * @param id
     * @return
     */
    @RequestMapping(value = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        //更新数据库
        discussPostService.updateStatus(id,1);
        //由于ES中也保存了帖子数据，应该需要异步的把更新提交到MQ
        Event event = new Event()
                .setEntityId(id)
                .setEntityType(ENTITY_TYPE_POST)
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId());
        eventProducer.fireEvent(event);

        discussPostService.waitToUpdateScore(id);

        //返回
        return CommunityUtil.getJsonString(0);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        //更新数据库
        discussPostService.updateStatus(id,2);
        //由于ES中也保存了帖子数据，应该从ES中删除帖子
        Event event = new Event()
                .setEntityId(id)
                .setEntityType(ENTITY_TYPE_POST)
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId());
        eventProducer.fireEvent(event);
        //返回
        return CommunityUtil.getJsonString(0);
    }

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
            comment.setLikeStatus(curUser == null ? 0 :
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
     * 发布post，
     * 如果用户未登录，发帖失败，
     *
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
        //将discussPost也存到ES中(异步)
        Event event = new Event().setTopic(TOPIC_PUBLISH).setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        //新增帖子的score待更新
        discussPostService.waitToUpdateScore(discussPost.getId());

        //返回结果
        return CommunityUtil.getJsonString(0,"发布成功！");
    }
}
