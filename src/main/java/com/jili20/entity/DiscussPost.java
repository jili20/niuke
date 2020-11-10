package com.jili20.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author bing  @create 2020/11/3-2:53 下午
 */
@Data
@ToString
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;  // 一定要用 int 类型，默认值为 0，不会空指针异常
    private int  status;
    private Date createTime;
    private int commentCount;
    private double score;
}
