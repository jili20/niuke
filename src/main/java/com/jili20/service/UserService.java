package com.jili20.service;

import com.jili20.dao.LoginTicketMapper;
import com.jili20.dao.UserMapper;
import com.jili20.entity.LoginTicket;
import com.jili20.entity.User;
import com.jili20.util.CommunityConstant;
import com.jili20.util.CommunityUtil;
import com.jili20.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author bing  @create 2020/11/3-8:00 下午
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;


    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    // 注册
    public Map<String, Object> register(User user) { // 此 user 是页面传进来的
        Map<String, Object> map = new HashMap<>();

        // 空判断
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernamMsg", "账号不能为空!");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号邮箱是否存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernamMsg", "该账号已存在！");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "邮箱已被注册!");
            return map;
        }
        // 注册用户
        // 处理密码加密
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));

        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID()); // 激活码
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user); // 配置文件中配置了自动生成id的机制

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail()); // 发送给那个用户
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    // 处理激活状态
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACCTIVATION_FAILURE;
        }
    }

    // 登录
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }
        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    // 退出
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    // 查询ticker
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicker(ticket);
    }

    // 更新头像
    // int 类型是影响行数
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }


    // 修改密码
    public int updatePassword(int id, String password) {
        return userMapper.updatePassword(id, password);
    }

    // 修改用户名
    public int updateUsername(int id, String username) {
        return userMapper.updateUsername(id, username);
    }

    // 修改邮箱
    public int updateEmail(int id, String email) {
        return userMapper.updateEmail(id, email);
    }


    // 验证用户输入的找回密码的邮箱 SendPassword
    public Map<String, Object> SendPassword(String email) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证邮箱是否存在
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱不存在！");
            return map;
        }

        String temporary = CommunityUtil.generateUUID().substring(0, 8) ;
        String password =(CommunityUtil.md5(temporary + user.getSalt()));
        userMapper.updatePassword(user.getId(),password);

        // 发送邮件
        Context context = new Context();
        context.setVariable("email", email); // 发送给那个用户
        context.setVariable("password", temporary); // 发送给那个用户
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        String content = templateEngine.process("/mail/sendcode", context); // 用户邮箱收到的信息模板
        mailClient.sendMail(email, "找回密码", content);

        return map;
    }

    // 通过用户名查询用户
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
}
