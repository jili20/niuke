package com.jili20.service;

import com.jili20.dao.CommentMapper;
import com.jili20.entity.Comment;
import com.jili20.util.CommunityConstant;
import com.jili20.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
/**
 * @author bing  @create 2020/11/10-9:35 上午
 */
@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    // 分页查询评论
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    // 查询评论总数
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    // 添加评论；使用事务，READ_COMMITTED 隔离级别，REQUIRED 传播机制
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent())); // html 转义
        comment.setContent(sensitiveFilter.filter(comment.getContent())); // 过滤敏感词
        int rows = commentMapper.insertComment(comment);
        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 先查出帖子的评论数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            // 根据帖子id 更新评论数量
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }
        return rows;
    }
}
