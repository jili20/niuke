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
import org.springframework.web.bind.annotation.PathVariable;

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
        page.setRows(messageService.findConversationCount(user.getId())); // 当前用户会话总数

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
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        return "/site/letter";
    }

    // 私信详情列表
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 设置分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        // 私信列表
        List<Message> letterList = messageService.findLdtters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList !=null ) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        model.addAttribute("target",getLetterTarget(conversationId));
        return "/site/letter-detail";
    }

    // 查出与当前用户对话的用户（上面私信详情列表用）
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }

}





