package com.starry.community.controller.interceptor;

import com.starry.community.annotation.CheckLogin;
import com.starry.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Starry
 * @create 2022-09-06-5:32 PM
 * @Describe 用于检查用户是否登录，对于那些需要登录的handler，需要登录才能通过拦截器，如果没有登录则跳转到登录页面
 */
@Component
@Deprecated
public class CheckLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            CheckLogin annotation = method.getAnnotation(CheckLogin.class);
            //假如该方法上有CheckLogin注解
            if (annotation != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }

}
