package com.starry.community.util;

import com.starry.community.bean.User;
import org.springframework.stereotype.Component;

/**
 * @author Starry
 * @create 2022-09-06-1:10 AM
 * @Describe 容器，持有用户信息，用于代替Session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }

}
