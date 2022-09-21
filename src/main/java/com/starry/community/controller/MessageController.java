package com.starry.community.controller;

import com.alibaba.fastjson2.JSONObject;
import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.Message;
import com.starry.community.bean.Page;
import com.starry.community.bean.User;
import com.starry.community.service.MessageService;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author Starry
 * @create 2022-09-12-1:42 PM
 * @Describe
 */
@Controller
@RequestMapping("/message")
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    /**
     * 获取通知的详情页，前端需要传入分页参数以及要查看的通知的主题
     * @param topic 需要跳转的通知的主题类型
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(value = "/notification/detail/{topic}", method = RequestMethod.GET)
    @CheckLogin
    public String getNotificationDetail(@PathVariable("topic") String topic,Model model,Page page) {
        if (StringUtils.isBlank(topic)) {
            throw new RuntimeException();
        }
        int userId = hostHolder.getUser().getId();
        //设置page
        page.setLimit(5);
        page.setPath("/message/notification/detail/" + topic);
        page.setRows(messageService.findNotificationsCountByUserIdAndTopic(userId,topic));
        //
        List<Message> notifications =
                messageService.findNotifications(userId, topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVO = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        if (notifications != null) {
            for (Message notification : notifications) {
                ids.add(notification.getId());
                Map<String, Object> map = new HashMap<>();
                map.put("message", notification);
                //解析message中的content，提取出引起事件的用户等信息，并存入map以供显示。
                String content = notification.getContent();
                //将content，反转义，然后解析为HashMap,以便提取数据
                Map data = JSONObject.parseObject(HtmlUtils.htmlUnescape(content), HashMap.class);
                //根据data中的userId，find到user对象(引起通知事件的那个user)
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId", data.get("postId"));
                noticeVO.add(map);
            }
        }
        //更新message的status为1(已读)
        messageService.readMessagesStatus(ids);

        model.addAttribute("notifications", noticeVO);
        return "/site/notice-detail";
    }


    /**
     * 用户查看自己的系统通知列表，该方法应该查询所有topic的最新通知，并封装到model里
     * @param model
     * @return
     */
    @RequestMapping(value = "/notification/list",method = RequestMethod.GET)
    @CheckLogin
    public String getNotificationPage(Model model) {
        String[] topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW};
        for (String topic : topics) {
            String key = topic + "Notification";
            Map<String,Object> value = getNotificationByTopic(topic);
            model.addAttribute(key, value);
        }
        return "site/notice";
    }

    /**
     * 一个通知的需要包括的信息有:
     * createTime(通知的时间)
     * user(哪个用户对当前用户发起了点赞、评论、关注等事件),
     * entityType(引起通知的类型),
     * entityId(引起通知的具体id，方便跳转),
     * count(该类型通知的总数量),
     * unread(该类型通知的总的未读数量)
     * postId(当topic为评论和点赞的时候有，关注则没有，是引起通知所在的帖子，方便跳转)
     * @return 将所有所需信息封装进map中并返回
     */
    private Map<String, Object> getNotificationByTopic(String topic) {
        Map<String, Object> map = new HashMap<>();
        Message message = messageService.findLatestNotificationByUserIdAndTopic(hostHolder.getUser().getId(), topic);
        if (message != null) {
            map.put("createTime",message.getCreateTime());
            Map hashMap = JSONObject.parseObject(HtmlUtils.htmlUnescape(message.getContent()), HashMap.class);
            if (hashMap == null) {
                throw new RuntimeException("未知异常");
            }
            //根据userId查找user并存入map
            map.put("user",userService.findUserById((Integer) hashMap.get("userId")));
            map.put("entityType",hashMap.get("entityType"));
            map.put("entityId",hashMap.get("entityId"));
            //该topic的通知的总数量
            int count = messageService.findNotificationsCountByUserIdAndTopic(hostHolder.getUser().getId(), topic);
            map.put("count", count);
            //该topic的通知的未读总数量
            int unread = messageService.findUnreadNotificationsCountByUserIdAndTopic(hostHolder.getUser().getId(),topic);
            map.put("unread", unread);
            //假如需要postId
            if (hashMap.containsKey("postId")) {
                map.put("postId", hashMap.get("postId"));
            }
        }
        return map;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    @CheckLogin
    public String sendMessage(String toName, String content) {
        User targetUser = userService.findUserByUsername(toName);
        if (targetUser == null) {
            return CommunityUtil.getJsonString(1, "目标用户不存在！");
        }
        if (StringUtils.isBlank(content)) {
            return CommunityUtil.getJsonString(2, "回复内容不能为空！");
        }
        Message message = new Message();
        message.setContent(content);
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(targetUser.getId());
        message.setCreateTime(new Date());
        String conversationId;
        if (message.getFromId() < message.getToId()) {
            conversationId = message.getFromId() + "_" + message.getToId();
        } else {
            conversationId = message.getToId() + "_" + message.getFromId();
        }
        message.setConversationId(conversationId);
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    /**
     * 跳转到消息页面
     * 需要查到消息页面的相关数据，封装到model里
     *
     * @param page
     * @param model
     * @return
     */
    @RequestMapping("/getPage")
    @CheckLogin
    public String toMessagePage(Page page, Model model) {
        page.setPath("/message/getPage");
        page.setLimit(5);
        User user = hostHolder.getUser();
        page.setRows(messageService.findConversationsRows(user.getId()));
        List<Message> conversations = messageService.findConversations
                (user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        if (conversations != null) {
            for (Message message : conversations) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("unreadMessage", messageService.findUnreadMessagesCount
                        (user.getId(), message.getConversationId()));
                map.put("messageCount", messageService.findMessagesRowsByConversationId
                        (message.getConversationId()));
                //对方的userId
                int targetUserId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("targetUser", userService.findUserById(targetUserId));
                list.add(map);
            }
        }
        model.addAttribute("list", list);
        return "/site/letter";
    }


    @RequestMapping("/getDetailPage/{conversationId}/{targetUserId}")
    @CheckLogin
    public String getMessageDetailPage
            (@PathVariable("conversationId") String conversationId, Model model,
             Page page, @PathVariable("targetUserId") int targetUserId) {
        page.setRows(messageService.findMessagesRowsByConversationId(conversationId));
        page.setPath("/message/getDetailPage/" + conversationId + "/" + targetUserId);
        List<Message> messages =
                messageService.findMessagesByConversationId(conversationId, page.getOffset(), page.getLimit());
        List<Integer> ids = new ArrayList<>();
        int userId = hostHolder.getUser().getId();
        for (Message message : messages) {
            if (message.getToId() == userId && message.getStatus() == 0) {
                ids.add(message.getId());
            }
        }
        messageService.readMessagesStatus(ids);
        model.addAttribute("messages", messages);
        User targetUser = userService.findUserById(targetUserId);
        model.addAttribute("targetUser", targetUser);
        return "site/letter-detail";
    }
}
