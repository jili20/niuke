package com.jili20.dao;

import com.jili20.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author bing  @create 2020/11/3-2:56 下午
 */
@Mapper
public interface DiscussPostMapper {

    // offset 起始行号（页数）；limit每页显示多少条数据
//    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param 注解用于给参数取别名
    // 如果只有一个参数，并且在 <if> 里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //添加帖子
    int insertDiscussPost(DiscussPost discussPost);

    //帖子详情（根据帖子id查询帖子详情）
    DiscussPost selectDiscussPostById(int id);
//    //更新帖子的数量
//    int updateCommentCount(int id, int commentCount);
//    // 根据id修改类型
//    int updateType(int id, int type);
//    // 根据id修改状态
//    int updateStatus(int id, int status);
//    // 更新帖子分数
//    int updateScore(int id, double score);
}

