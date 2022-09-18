package com.starry.community.controller;

import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.Event;
import com.starry.community.bean.User;
import com.starry.community.event.EventProducer;
import com.starry.community.service.CommentService;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.LikeService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author Starry
 * @create 2022-09-14-2:38 PM
 * @Describe    处理用户点赞相关请求
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private CommentService commentService;

    /**
     * 用户点赞，将当前实体的点赞数和用户点赞状态返回给前端
     * @param entityType 点赞的实体的实体类型
     * @param entityId 点赞的实体的实体id
     * @param targetUserId 点赞的实体的所属user的id
     * @param postId 点赞所发生的帖子的id
     * @return
     */
    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String likeEntity(int entityType,int entityId, int targetUserId, int postId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(1,"请先登录！");
        }
        likeService.like(entityType, entityId, user.getId(), targetUserId);
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.isUserLikeEntity(entityType, entityId, user.getId());

        //如果是成功点赞，就将点赞事件放到消息队列中，等待消费者生成通知
        if (likeStatus == 1) {
            Event event = new Event()
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityUserId(targetUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("count",entityLikeCount);
        map.put("likeStatus",likeStatus);
        return CommunityUtil.getJsonString(0, null, map);
    }
}
