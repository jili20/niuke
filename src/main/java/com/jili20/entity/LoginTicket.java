package com.jili20.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author bing  @create 2020/11/4-8:48 下午
 */
@ToString
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
