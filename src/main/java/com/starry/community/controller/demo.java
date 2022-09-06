package com.starry.community.controller;

import com.starry.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Starry
 * @create 2022-09-05-9:26 AM
 * @Describe
 */
@Controller
public class demo {
    @RequestMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie("key", CommunityUtil.generateUUID());
        cookie.setPath("/community/cookie/get");
        cookie.setMaxAge(60 * 10);
        httpServletResponse.addCookie(cookie);
        return "index";
    }

    @RequestMapping("/cookie/get")
    @ResponseBody
    public Cookie[] getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return cookies;
    }

    @RequestMapping("/session/set")
    @ResponseBody
    public String SessionTest(HttpSession httpSession) {
        httpSession.setAttribute("k1", "v1");
        httpSession.setAttribute("k2", "v2");
        return "hi";
    }

    @RequestMapping("/session/get")
    @ResponseBody
    public String SessionTest2(HttpSession session) {
        System.out.println(session);
        System.out.println(session.getAttribute("k1"));
        return "hi";
    }
}



