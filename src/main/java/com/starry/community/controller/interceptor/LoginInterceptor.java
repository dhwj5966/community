package com.starry.community.controller.interceptor;


import com.starry.community.bean.User;
import com.starry.community.service.MessageService;
import com.starry.community.service.UserService;
import com.starry.community.util.CookieUtil;
import com.starry.community.util.HostHolder;
import com.starry.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-06-12:00 AM
 * @Describe 在执行handler之前，根据Cookie检查用户是否是已登录用户，如果是则把user对象存放到HostHolder中
 * 并在使用完后clear HostHolder
 */
@Component
@Order(value = Integer.MAX_VALUE - 1)
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Cookie cookie = CookieUtil.getCookieByName(request, "ticket");
        if (cookie != null) {
            String ticket = cookie.getValue();
            User user = userService.findLoginUserByTicket(ticket);
            //验证凭证有效，不为空，Status==0，未过期
            if (user != null) {
                hostHolder.setUser(user);
                /*
                    构建用户认证的结果，并存入SecurityContextHolder,完成SpringSecurity的授权。
                 */
                Authentication authentication = new UsernamePasswordAuthenticationToken
                        (user, user.getPassword(),userService.getAuthority(user.getType()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }

        return true;
    }

    /**
     * 该方法会在handler执行完，render前执行，因此在这里将user对象取出放到model中，方便渲染。
     * 并且将已登录用户、未读私信和未读通知数都存入modelAndView中。
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
            int unreadMessagesCount = messageService.findUnreadMessagesCount(user.getId(), null);
            modelAndView.addObject("unread",unreadMessagesCount);
            int unreadNotification = messageService.findUnreadNotificationsCountByUserIdAndTopic(user.getId(),null);
            modelAndView.addObject("unreadNotification",unreadNotification);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();//help GC
    }
}
