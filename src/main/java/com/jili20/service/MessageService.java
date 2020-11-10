package com.jili20.service;

import com.jili20.dao.MessageMapper;
import com.jili20.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bing  @create 2020/11/10-8:14 下午
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    // 所有会话列表
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    // 与 111 会话数量
    public int findConversationCout(int userId){
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

}
