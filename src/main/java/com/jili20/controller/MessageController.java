package com.jili20.controller;

import com.jili20.entity.Message;
import com.jili20.entity.Page;
import com.jili20.entity.User;
import com.jili20.service.MessageService;
import com.jili20.service.UserService;
import com.jili20.util.CommunityUtil;
import com.jili20.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
//        Integer.valueOf("abc"); // 人为制造错误，测试系统异常处理
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
        //分页信息
        page.setLimit(5);//每页显示5条
        page.setPath("/letter/detail/" + conversationId); // 当前路径
        page.setRows(messageService.findLetterCount(conversationId));//设置行数
        //私信列表  findLdtters
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        //查询私信目标，发送给模板显示
        model.addAttribute("target", getLetterTarget(conversationId));

        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    // 私信目标
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");//拆分成两个id
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    // 获取未读消息所有id（设置为已读用）
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                // 如果我是接受者身份，并且消息处于 未读状态
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    // 发送私信
    @PostMapping("/letter/send")
    @ResponseBody // 异步
    public String sendLetter(String toName, String content) {
//        Integer.valueOf("abc"); // 人为制造错误，测试系统异常处理
        // 发送的目标（发给谁）
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId()); // 发件人，当前登录用户
        message.setToId(target.getId()); // 收件人
        // 拼会话id
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }


}





