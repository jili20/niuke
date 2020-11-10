package com.jili20.dao;

import com.jili20.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author bing  @create 2020/11/10-9:15 上午
 */
@Mapper
public interface CommentMapper {
    // 分页查询评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    // 查询评论总数
    int selectCountByEntity(int entityType,int entityId);

    // 添加评论，返回影响行数
    int insertComment(Comment comment);

}
