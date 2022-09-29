package com.starry.community.service.impl;

import com.starry.community.bean.User;
import com.starry.community.mapper.UserMapper;
import com.starry.community.service.UserService;
import com.starry.community.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Starry
 * @create 2022-09-02-6:18 PM
 * @Describe
 */
@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 尝试从缓存中读取User数据，如果不存在则返回null
     */
    private User getCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    /**
     * 从Mysql数据库中，根据userId查到user对象，并存入Redis
     */
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        redisTemplate.opsForValue().set(RedisKeyUtil.getUserKey(userId),user,3600, TimeUnit.SECONDS);
        return user;
    }
    /**
     * 清除缓存数据
     */
    private void clearCache(int userId) {
        redisTemplate.delete(RedisKeyUtil.getUserKey(userId));
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthority(int type) {
        List<GrantedAuthority> result = new ArrayList<>();
        result.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (type) {
                    case 1 :
                        return AUTHORITY_ADMIN;
                    case 2 :
                        return AUTHORITY_MODERATOR;
                    default :
                        return AUTHORITY_USER;
                }
            }
        });
        return result;
    }

    @Override
    public int updatePasswordById(int userId, String targetPassword) {
        if (StringUtils.isBlank(targetPassword)) {
            throw new IllegalArgumentException("不能将密码修改为空");
        }
        return userMapper.updatePasswordById(userId, targetPassword);
    }

    @Override
    public int updateHeaderUrl(int userId, String headerUrl, String ticket) {
        if (StringUtils.isBlank(headerUrl)) {
            throw new RuntimeException("头像Url不能为空");
        }
        int i = userMapper.updateHeaderById(userId, headerUrl);
        //如果真的修改了数据库中的数据，则清理缓存
        if (i != 0) {
            clearCache(userId);
            //更新Redis中login:ticket -> user 数据中，user的headerUrl
            if (!StringUtils.isBlank(ticket)) {
                String loginUserKey = RedisKeyUtil.getLoginUserKey(ticket);
                User user = (User) redisTemplate.opsForValue().get(loginUserKey);
                if (user != null) {
                    user.setHeaderUrl(headerUrl);
                    Long timeToExpire = redisTemplate.getExpire(loginUserKey);
                    redisTemplate.opsForValue().set(loginUserKey, user,timeToExpire, TimeUnit.SECONDS);
                }
            }

        }
        return i;
    }


    @Override
    public void logOut(String ticket) {
        String loginUserKey = RedisKeyUtil.getLoginUserKey(ticket);
        redisTemplate.expire(loginUserKey, -1, TimeUnit.SECONDS);
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
        //生成key,存入redis,把tick封装到map中返回给表现层
        String ticket = CommunityUtil.generateUUID();
        String key = RedisKeyUtil.getLoginUserKey(ticket);
        redisTemplate.opsForValue().set(key,user,expiredSeconds, TimeUnit.SECONDS);
        map.put("ticket", ticket);
        return map;
    }

    @Override
    public User findUserById(int id) {
        //采用缓存策略
        //1.从缓存中查询
        User cache = getCache(id);
        if (cache != null) {
            return cache;
        }
        //2.如果没查到，从数据库中查,并更新缓存
        return initCache(id);
    }

    @Override
    public User findLoginUserByTicket(String ticket) {
        String loginUserKey = RedisKeyUtil.getLoginUserKey(ticket);
        User user = (User) redisTemplate.opsForValue().get(loginUserKey);

        return user;
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
            clearCache(id);
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







