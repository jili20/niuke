package com.jili20.service;

import com.jili20.dao.CommentMapper;
import com.jili20.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author bing  @create 2020/11/10-9:35 上午
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    // 分页查询评论
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    // 查询评论总数
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }
}
