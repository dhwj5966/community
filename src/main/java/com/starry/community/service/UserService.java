package com.starry.community.service;

import com.starry.community.bean.LoginTicket;
import com.starry.community.bean.User;

import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-02-6:18 PM
 * @Describe
 */
public interface UserService {


    /**
     * 修改指定userId的用户的密码
     */
    int updatePasswordById(int userId, String targetPassword);

    /**
     * 修改指定id的用户的头像的路径
     *
     * @param userId
     * @param headerUrl
     * @return
     */
    int updateHeaderUrl(int userId, String headerUrl);

    LoginTicket findLoginTicketByTicket(String ticket);

    /**
     * 根据传入的凭证，将登录状态改为未登录,即status改为0
     *
     * @param ticket
     */
    void logOut(String ticket);

    /**
     * 根据用户名、密码、过期时间，验证是否登录成功
     * 如果登录成功，将凭证存入login_ticket表,并将ticket封装到map中返回
     * 如果登录失败，将登录失败的原因封装到map中返回
     */
    Map<String, Object> login(String username, String password, Long expiredSeconds);

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    User findUserById(int id);

    /**
     * 根据传入的User对象，验证信息并完成注册，如果失败则返回带有失败信息的Map，
     * 如果成功则将user对象存入数据库并发送激活邮件给客户
     * @param user  带有账号、密码、邮箱的user，其他field为null
     * @return 带有处理详情的Map对象
     */
    Map<String, Object> register(User user);

    /**
     * 处理激活业务
     *
     * @param id
     * @param activationCode
     * @return
     */
    int activation(int id, String activationCode);

    /**
     * 根据username查询User对象
     *
     * @param username
     * @return 如果存在则返回User对象，否则返回null
     */
    User findUserByUsername(String username);
}
