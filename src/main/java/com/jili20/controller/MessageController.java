package com.jili20.controller;

import com.jili20.entity.Message;
import com.jili20.entity.Page;
import com.jili20.entity.User;
import com.jili20.service.MessageService;
import com.jili20.service.UserService;
import com.jili20.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bing  @create 2020/11/10-8:29 下午
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();

        // 设置分页信息
        page.setLimit(5);//每页显示5条
        page.setPath("/letter/list"); // 当前路径
        page.setRows(messageService.findConversationCout(user.getId())); // 当前用户会话总数

        // 所有会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());

        // 会话总数量
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversations != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                // 每个会话的消息总数
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                // 每个会话的未读消息数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                // 显示与当前用户通信的人的头像: 如果当前用户是发件人FromId，显示收件人ToId头像，否则显示发件人头像
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);

            }
        }
        model.addAttribute("conversations", conversations); // 分组会话 页面遍历这个
        // 查询未读消息总数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "/site/letter";
    }
}





