package com.jili20;

import com.jili20.dao.DiscussPostMapper;
import com.jili20.dao.LoginTicketMapper;
import com.jili20.dao.UserMapper;
import com.jili20.entity.DiscussPost;
import com.jili20.entity.LoginTicket;
import com.jili20.entity.User;
import com.jili20.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class Jili20ApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter; // 过滤敏感词

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());

    }


    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }


    @Test
    public void testSelectPost() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);

    }


    // 测试添加凭证
    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    // 测试查询凭证
    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicker("abc");
        System.out.println(loginTicket);

        // 更新凭证
        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicker("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void test() {
        System.out.println(System.currentTimeMillis()); // 打印当前时间戳
    }

    // 测试过滤敏感词
    @Test
    public void testSensitiveFilter() {
        String text = "这里可以赌博，可以嫖娼，可以吸毒，可以开票,哈哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        String text2 = "这里可以赌🟢博，可以嫖👍娼，可以吸🌺毒，可以开👉👉票,哈哈哈";
        text2 = sensitiveFilter.filter(text);
        System.out.println(text2);
    }


}
