package com.starry.community.controller;

import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.Event;
import com.starry.community.bean.Page;
import com.starry.community.bean.User;
import com.starry.community.event.EventProducer;
import com.starry.community.service.FollowService;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-15-9:37 AM
 * @Describe 关注模块表现层
 */
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    /**
     * 用户关注实体
     * @param entityType
     * @param entityId
     * @return
     */
    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    @CheckLogin
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        //关注成功后，给被关注的用户发通知
        Event event = new Event()
                .setUserId(user.getId())
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(0,"关注成功！");
    }

    /**
     * 用户取消关注实体
     * @param entityType
     * @param entityId
     * @return
     */
    @RequestMapping(value = "/unfollow",method = RequestMethod.POST)
    @CheckLogin
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0,"取关成功！");
    }

    /**
     * 跳转到指定用户的粉丝列表
     * @param userId 指定跳转到哪个用户的粉丝页面
     */
    @RequestMapping(value = "/follower/{userId}",method = RequestMethod.GET)
    public String getFollowerPage(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        page.setPath("/follower/" + userId);
        page.setRows((int) followService.getFollowersCount(userId));
        page.setLimit(10);
        List<Map<String, Object>> followers =
                followService.getFollowers(userId, page.getOffset(), page.getLimit());
        //补充当前用户对followers中用户的关注状态
        if (followers != null) {
            for (Map<String, Object> map : followers) {
                User follower = (User) map.get("user");
                boolean followStatus = getFollowStatus(follower.getId());
                map.put("followStatus",followStatus);
            }
        }
        model.addAttribute("followers",followers);
        model.addAttribute("user",user);
        return "/site/follower";
    }

    /**
     * 判断当前用户是否关注了userId指向的用户，如果关注了则返回true，如果没关注则返回false
     * @param userId
     * @return
     */
    private boolean getFollowStatus(int userId) {
        boolean result = false;
        if (hostHolder.getUser() != null) {
            result = followService.getFollowStatus(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        return result;
    }

    /**
     * 跳转到指定用户的关注列表
     * @param userId
     * @return
     */
    @RequestMapping(value = "/followee/{userId}", method = RequestMethod.GET)
    public String getFolloweePage(@PathVariable("userId") int userId, Page page, Model model) {
        //获取要查看的页面的用户，以便获取username等信息
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        page.setPath("/followee/" + userId);
        page.setRows((int) followService.getFolloweeCount(userId, ENTITY_TYPE_USER));
        page.setLimit(10);
        List<Map<String, Object>> followees =
                followService.getFollowees(userId, page.getOffset(), page.getLimit());
        //补充当前用户对followers中用户的关注状态
        if (followees != null) {
            for (Map<String, Object> map : followees) {
                User followee = (User) map.get("user");
                boolean followStatus = getFollowStatus(followee.getId());
                map.put("followStatus",followStatus);
            }
        }
        model.addAttribute("followees",followees);
        model.addAttribute("user",user);
        return "/site/followee";
    }

}
