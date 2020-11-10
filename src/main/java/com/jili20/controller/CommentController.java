package com.jili20.controller;

import com.jili20.entity.Comment;
import com.jili20.service.CommentService;
import com.jili20.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

/**
 * @author bing  @create 2020/11/10-1:19 下午
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    // 当前用户 添加评论
    @PostMapping("/add/{discussPostId}")
    private String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment, RedirectAttributes attr){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        int row = commentService.addComment(comment);
        if (row > 0) {
            attr.addFlashAttribute("addCommentMsg", "添加评论成功");
        }
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
