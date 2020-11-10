package com.jili20.controller;

import com.jili20.entity.Comment;
import com.jili20.entity.DiscussPost;
import com.jili20.entity.Page;
import com.jili20.entity.User;
import com.jili20.service.CommentService;
import com.jili20.service.DiscussPostService;
import com.jili20.service.UserService;
import com.jili20.util.CommunityConstant;
import com.jili20.util.CommunityUtil;
import com.jili20.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author bing  @create 2020/11/9-3:58 下午
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder; // 当前登录用户

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody // 异步传输数据
    private String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403,"您还没有登录！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        // 报错的情况将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    // 根据帖子id查询帖子详情
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        // 评论分页信息
        page.setLimit(5);//每页显示5条
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：给帖子的评论，一级评论
        // 回复：给评论的评论，二级评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 一级评论的 VO 列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论的 VO
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment); // 评论
                commentVo.put("user",userService.findUserById(comment.getUserId())); // 评论作者

                // 查询回复列表，二级评论列表； 二级评论有多少查多少不分页显示
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复 vo 列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList !=null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply); // 回复
                        replyVo.put("user",userService.findUserById(reply.getUserId())); // 作者
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                // 二级评论的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }


}
