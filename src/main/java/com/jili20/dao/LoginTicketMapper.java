package com.jili20.dao;

import com.jili20.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author bing  @create 2020/11/4-8:54 下午
 */
@Mapper
public interface LoginTicketMapper {
    // 插入凭证
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    // 查询凭证
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicker(String ticket);

    // 更新用户凭证状态
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
