package com.starry.community.controller;

import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.User;
import com.starry.community.service.LikeService;
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
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;


    /**
     * 用户点赞，将当前实体的点赞数和用户点赞状态返回给前端
     */
    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String likeEntity(int entityType,int entityId, int targetUserId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(1,"请先登录！");
        }
        likeService.like(entityType, entityId, user.getId(), targetUserId);
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.isUserLikeEntity(entityType, entityId, user.getId());

        HashMap<String, Object> map = new HashMap<>();
        map.put("count",entityLikeCount);
        map.put("likeStatus",likeStatus);
        return CommunityUtil.getJsonString(0, null, map);
    }
}
