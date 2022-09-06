package com.starry.community.controller.interceptor;

import com.starry.community.bean.LoginTicket;
import com.starry.community.bean.User;
import com.starry.community.service.UserService;
import com.starry.community.util.CookieUtil;
import com.starry.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Describe
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Cookie cookie = CookieUtil.getCookieByName(request, "ticket");
        if (cookie != null) {
            String ticket = cookie.getValue();
            LoginTicket lt = userService.findLoginTicketByTicket(ticket);
            //验证凭证有效，不为空，Status==0，未过期
            if (lt != null && lt.getStatus() == 0 && lt.getExpired().after(new Date())) {
                User user = userService.findUserById(lt.getUserId());
                //注意！这里不要直接存到request对象里，而是用ThreadLocal
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //该方法会在handler执行完，render前执行，因此在这里将user对象取出放到model中，方便渲染
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
