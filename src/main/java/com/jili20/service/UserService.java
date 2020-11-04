package com.jili20.service;

import com.jili20.dao.UserMapper;
import com.jili20.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author bing  @create 2020/11/3-8:00 下午
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
