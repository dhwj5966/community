package com.starry.community.controller;

import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.Comment;
import com.starry.community.bean.Event;
import com.starry.community.bean.User;
import com.starry.community.event.EventProducer;
import com.starry.community.service.CommentService;
import com.starry.community.service.DiscussPostService;
import com.starry.community.util.CommunityConstant;
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
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;

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
        commentService.addComment(comment);

        /*
            在用户发表评论后，还需要给该评论针对的实体的发布者，发送系统通知。
            只需要根据已有条件封装好Event对象，
            然后把Event对象，通过eventProducer的fireEvent方法，存放到Kafka中，Kafka的消费者会异步地进行处理。
         */

        Event event = new Event()
                        .setUserId(user.getId())
                        .setTopic(TOPIC_COMMENT)
                        .setEntityType(comment.getEntityType())
                        .setEntityId(comment.getEntityId())
    //由于评论要么是对帖子的评论，要么是对帖子的评论的回复，因此评论必然有所属的postId，将postId封装到map中(其实也可以作为Field出现)
                        .setData("postId",discussPostId);
        //还需要找到评论针对的实体的userId，这需要去查表，查哪个表要看评论针对的实体类型，可能是查帖子表，也可能是查评论表
        int entityUserId = 0;
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            //如果评论针对的实体是帖子，即评论是对帖子的评论
            entityUserId = discussPostService.findDiscussPostById(comment.getEntityId()).getUserId();
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            entityUserId = commentService.findCommentById(comment.getEntityId()).getUserId();
        }
        event.setEntityUserId(entityUserId);
        eventProducer.fireEvent(event);

        return "redirect:/discussPost/showPostDetail/" + discussPostId;
    }
}
