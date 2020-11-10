package com.jili20.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author bing  @create 2020/11/10-5:24 下午
 */
@Data
@ToString
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

}
