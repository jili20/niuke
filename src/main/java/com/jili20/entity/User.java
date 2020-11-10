package com.jili20.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author bing  @create 2020/11/2-10:33 下午
 */
@ToString
@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
