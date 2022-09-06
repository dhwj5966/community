package com.starry.community.controller;

import com.starry.community.bean.User;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-03-7:37 PM
 * @Describe 负责用户注册与激活
 */
@Controller
public class RegisterController implements CommunityConstant {
    @Autowired
    private UserService userService;

    /**
     * 跳转到注册页面
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String goRegisterPage() {
        return "/site/register";
    }

    /**
     * 接收注册请求
     *
     * @param model
     * @param user
     * @return 提交注册请求后跳转到/site/operate-result
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> register = userService.register(user);
        if (register == null || register.isEmpty()) {
            model.addAttribute("msg", "注册成功,已向您的邮箱发送激活邮件,请及时查看");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", register.get("usernameMsg"));
            model.addAttribute("emailMsg", register.get("emailMsg"));
            model.addAttribute("passwordMsg", register.get("passwordMsg"));
            return "/site/register";
        }
    }

    /**
     * 根据id和code,进行激活,不管激活结果如何，都跳转到operate-result页面，进行提示与跳转
     */
    //http://localhost:8080/community/activation/{id}/{activationCode}
    @RequestMapping(value = "/activation/{id}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable(value = "id") int id,
                             @PathVariable(value = "code") String code) {
        int result = userService.activation(id, code);
        if (result == ACTIVATION_SUCCESS) {
            //如果成功了，就跳到登录页面
            model.addAttribute("msg", "激活成功，即将跳转到牛客网的登录页面");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过！");
            model.addAttribute("target", "/index");
        } else if (result == ACTIVATION_FALIURE) {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

}

