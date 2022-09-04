package com.starry.community.service;

import com.starry.community.bean.User;

import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-02-6:18 PM
 * @Describe
 */
public interface UserService {
    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User findUserById(int id);

    /**
     * 根据传入的User对象，验证信息并完成注册，如果失败则返回带有失败信息的Map，
     * 如果成功则将user对象存入数据库并发送激活邮件给客户
     * @param user  带有账号、密码、邮箱的user，其他field为null
     * @return  带有处理详情的Map对象
     */
    Map<String,Object> register(User user);

    /**
     * 处理激活业务
     * @param id
     * @param activationCode
     * @return
     */
    int activation(int id, String activationCode);
}
