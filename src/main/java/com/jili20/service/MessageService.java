package com.jili20.service;

import com.jili20.dao.MessageMapper;
import com.jili20.entity.Message;
import com.jili20.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author bing  @create 2020/11/10-8:14 下午
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 所有会话列表
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    // 与 111 会话数量
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    // 某会话列表
    public List<Message> findLdtters(String conversationId,int offset, int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    // 每个会话的消息总数
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    // 每个会话的未读消息数量
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    // 发送私信
    public int addMessage(Message message){
        // 转义，过滤敏感词后插入数据库
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    // 修改未读私信状态为已读
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }
}
