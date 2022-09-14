package com.starry.community.controller;

import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.Message;
import com.starry.community.bean.Page;
import com.starry.community.bean.User;
import com.starry.community.service.MessageService;
import com.starry.community.service.UserService;
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

import java.util.*;

/**
 * @author Starry
 * @create 2022-09-12-1:42 PM
 * @Describe
 */
@Controller
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

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
