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
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private Integer type;
    private Integer  status;
    private Date createTime;
    private Integer commentCount;
    private double score;
}
