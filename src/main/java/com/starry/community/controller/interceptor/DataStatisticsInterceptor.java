package com.starry.community.controller.interceptor;

import com.starry.community.bean.User;
import com.starry.community.service.DataService;
import com.starry.community.util.HostHolder;
import com.starry.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Starry
 * @create 2022-09-26-2:03 PM
 * @Describe    用于数据统计的拦截器
 */
@Component
@Order(value = Integer.MAX_VALUE)
public class DataStatisticsInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DataService dataService;

    /**
     * 在访问之前进行数据统计
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String ip = request.getRemoteHost();
        dataService.setUV(ip);
        dataService.setDAU(hostHolder.getUser());
        return true;
    }
}
