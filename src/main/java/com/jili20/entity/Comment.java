package com.jili20.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author bing  @create 2020/11/9-10:35 下午
 */
@ToString
@Data
public class Comment {

    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

}
