package com.starry.community.controller.advice;

import com.starry.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Starry
 * @create 2022-09-13-10:04 AM
 * @Describe    发生异常后的统一处理
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    /**
     * 发生的所有Exception，会经过该方法
     * @param e
     * @param response
     * @param request
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        String XReuqestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(XReuqestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJsonString(1,"服务器异常"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
