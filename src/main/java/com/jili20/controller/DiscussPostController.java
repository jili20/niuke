package com.jili20.controller;

import com.jili20.entity.DiscussPost;
import com.jili20.entity.User;
import com.jili20.service.DiscussPostService;
import com.jili20.service.UserService;
import com.jili20.util.CommunityUtil;
import com.jili20.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author bing  @create 2020/11/9-3:58 下午
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder; // 当前登录用户

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
        post.setStatus(0);
        post.setType(0);
        post.setCommentCount(0);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        // 报错的情况将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    // 根据帖子id查询帖子详情
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        return "/site/discuss-detail";
    }


}
