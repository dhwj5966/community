package com.starry.community.controller;

import com.google.code.kaptcha.Producer;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Starry
 * @create 2022-09-03-7:37 PM
 * @Describe
 */
@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private Producer producer;

    //配置绑定
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 通过用户提交的表单，进行登录操作，
     * 如果登录成功，生成凭证存入数据库，并将凭证的ticket存放在Cookie中返回给客户端，然后跳转到首页。
     * 如果登录失败，跳转到登录页面，并展示失败原因。
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String Login(Model model, String username, String password, @CookieValue("kaptcha") String randomString,
                        String validCode, boolean rememberMe, HttpServletResponse response) {
        //检查验证码
        String kaptcha = null;
        if (StringUtils.isNotBlank(randomString)) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(randomString);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }
        if (StringUtils.isBlank(validCode) || StringUtils.isBlank(kaptcha) || !validCode.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }
        /*
            检查账号密码
         */
        Long expired = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expired);
        if (map.containsKey("ticket")) {
            //如果登录成功,并且成功生成了登录凭证
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expired.intValue());
            response.addCookie(cookie);
            //重定向到首页，为什么要用重定向？
            return "redirect:/index";
        } else {
            //如果登录失败，将失败原因封装到Model对象中并返回登录页
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 生成验证码文本，存入Redis中，key为随机生成的字符串，并且把这个key通过Cookie发给用户
     * 生成验证码的图片，通过怕response对象输出到页面上
     */
    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成验证码文本
        String text = producer.createText();
        //生成图片
        BufferedImage image = producer.createImage(text);
        //随机生成字符串
        String randomString = CommunityUtil.generateUUID();
        //客户端持有这个随机字符串
        Cookie cookie = new Cookie("kaptcha",randomString);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //利用随机生成的字符串，生成Redis的key,并将验证码存入Redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(randomString);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);
        //由于要通过response将图片输出到浏览器，因此需要设置ContentType
        response.setContentType("image/png");
        response.addCookie(cookie);
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (Exception e) {
            logger.error("生成验证码失败:" + e.getMessage());
        }
    }

    /**
     * 退出登录
     *
     * @return 重定向到登录页面
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut(@CookieValue("ticket") String ticket) {
        userService.logOut(ticket);
        return "redirect:/login";
    }
}
