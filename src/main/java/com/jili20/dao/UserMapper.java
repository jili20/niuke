package com.jili20.dao;

import com.jili20.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author bing  @create 2020/11/2-10:39 下午
 */
@Mapper
public interface UserMapper {

    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id, String password);
    int updateUsername(int id,String username);
    int updateEmail(int id,String email);


}
