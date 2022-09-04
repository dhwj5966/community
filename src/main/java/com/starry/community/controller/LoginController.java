package com.starry.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Starry
 * @create 2022-09-03-7:37 PM
 * @Describe
 */
@Controller
public class LoginController {

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String Login() {
        return null;
    }
}
