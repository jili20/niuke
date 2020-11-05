package com.jili20.util;

import com.jili20.entity.User;
import org.springframework.stereotype.Component;

/**
 * 技术用户信息，用于代替 session 对象
 * @author bing  @create 2020/11/5-2:26 下午
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
