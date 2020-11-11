package com.jili20.dao;

import com.jili20.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author bing  @create 2020/11/10-5:36 下午
 */
@Mapper
public interface MessageMapper {

    // 1、查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 2、查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 3、查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 4、查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 5、查询未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);

    // 6、新增消息
    int insertMessage(Message message);

    // 7、修改消息的状态(设置为已读，或删除）
    int updateStatus(List<Integer> ids, int status);
}
