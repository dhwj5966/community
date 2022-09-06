package com.starry.community.service.impl;

import com.starry.community.bean.LoginTicket;
import com.starry.community.bean.User;
import com.starry.community.mapper.LoginTicketMapper;
import com.starry.community.mapper.UserMapper;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.EmailSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Starry
 * @create 2022-09-02-6:18 PM
 * @Describe
 */
@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Value("http://localhost:8080/")
    private String domain;

    @Value("community")
    private String contextPath;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Override
    public int updatePasswordById(int userId, String targetPassword) {
        if (StringUtils.isBlank(targetPassword)) {
            throw new IllegalArgumentException("不能将密码修改为空");
        }
        return userMapper.updatePasswordById(userId, targetPassword);
    }

    @Override
    public int updateHeaderUrl(int userId, String headerUrl) {
        if (StringUtils.isBlank(headerUrl)) {
            throw new RuntimeException("头像Url不能为空");
        }
        return userMapper.updateHeaderById(userId, headerUrl);
    }

    public LoginTicket findLoginTicketByTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public void logOut(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public Map<String, Object> login(String username, String password, Long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //先验证参数格式
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        //验证账号密码是否正确
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在!");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活!");
            return map;
        }
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误!");
            return map;
        }
        //生成凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        //首先，对传入的user对象进行检验，是否格式不合法
        if (user == null) {
            throw new IllegalArgumentException("user 参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号和邮箱是否已注册
        User user1 = userMapper.selectByUsername(user.getUsername());
        if (user1 != null) {
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        user1 = userMapper.selectByEmail(user.getEmail());
        if (user1 != null) {
            map.put("emailMsg","邮箱已注册");
            return map;
        }
        //通过上述验证则对用户进行注册
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        userMapper.insertUser(user);

        //向用户发送激活邮件
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        //http://localhost:8080/community/activation/{id}/{activationCode}
        StringBuilder link = new StringBuilder(domain);
        link.append(contextPath);
        link.append("/activation/");
        link.append(user.getId());
        link.append("/");
        link.append(user.getActivationCode());
        context.setVariable("link",link.toString());
        String process = templateEngine.process("mail/activation", context);
        emailSender.sendMail(user.getEmail(), "牛客网-账号激活",process);
        return null;
    }

    @Override
    public int activation(int id, String activationCode) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ACTIVATION_FALIURE;
        } else if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(activationCode)) {
            userMapper.updateStatusById(id, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FALIURE;
        }
    }

    @Override
    public User findUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}







