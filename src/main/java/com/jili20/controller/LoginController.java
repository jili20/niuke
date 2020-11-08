package com.jili20.controller;

import com.google.code.kaptcha.Producer;
import com.jili20.dao.UserMapper;
import com.jili20.util.CommunityUtil;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import com.jili20.entity.User;
import com.jili20.service.UserService;
import com.jili20.util.CommunityConstant;
import com.jili20.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bing  @create 2020/11/4-12:18 下午
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired // 验证码配置类
    private Producer kaptchaProducer;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    // 注册
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/"); // 8 秒后跳到首页
            return "/site/operate-result"; // 操作结果页面
        } else {
            model.addAttribute("usernamMsg", map.get("usernamMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 处理激活状态
    // http://localhost:8080/community/activation/101/code
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "注册成功，您的账号可以正常使用了！");
            model.addAttribute("target", "/login"); // 8 秒后跳到首页
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了！");
            model.addAttribute("target", "/"); // 8 秒后跳到首页
        } else {
            model.addAttribute("msg", "激活失败，你提供的激活码不正确！");
            model.addAttribute("target", "/"); // 8 秒后跳到首页
        }
        return "/site/operate-result"; // 操作结果页面
    }

    // 获取生成验证码图片
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText(); // 根据配置生成4位字符串
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入session
        session.setAttribute("kaptcha", text);
        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    // 登录
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean remember, Model model,
                        HttpSession session, HttpServletResponse response, RedirectAttributes attr) {
        // 检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha"); // 验证码 返回是对象，强转字符串
        // equalsIgnoreCase 比对忽略大小写； kaptcha session验证码，code 用户输入的验证码
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }
        // 检查账号，密码
        // 是否勾选记住我
        int expiredSeconds = remember ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        // Date outDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);// 30分钟后过期
//        int expiredSeconds = remember?new Date(System.currentTimeMillis() + 30 * 60 * 1000):
//                new Date(System.currentTimeMillis() + 30 * 60 * 1000); // 8640000 // =100天；43200=12小时; // 2592000=30天

        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        // containsKey 包含了
        if (map.containsKey("ticket")) {
            // 存 cookie 给客户端
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath); // 有效范围
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie); // 发送给浏览器
            attr.addFlashAttribute("welcome", "欢迎 " + username + " ！");
            return "redirect:/"; //重定向到首页
        } else {
            // 处理没有 ticket
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    // 退出
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }


    @GetMapping("/forgetPassword")
    public String forgetPassword() {
        return "/mail/forget";
    }


    @GetMapping("/sendPassword")
    public String sendPassword() {
        return "/mail/password";
    }


    // 找回密码页面，获取用户提交的邮箱、验证码，校对邮箱是否存在，通过检查后，发送邮件验证码， sendPassword
    @PostMapping("/sendEmail")
    public String sendEmail(Model model, String email, String code, HttpSession session) {

        // 检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        // 同一页面，生成验证码时就存到 session 里了，现在拿出来比对
        if (StringUtils.isBlank(code)) {
            model.addAttribute("codeMsg", "请填写验证码！");
            return "/mail/forget";
        }

        if (StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/mail/forget";
        }

        Map<String, Object> map = userService.SendPassword(email);

        if (map == null || map.isEmpty()) {
            model.addAttribute("Msg.", "系统已为您随机生成了临时登录密码，已发送至 " + email + "邮箱中，请检查收件箱（或垃圾箱！），此信息十分钟内有效。");
            return "redirect:/sendPassword"; // /sendPassword 是方法路径，并非页面路径
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/mail/forget";
        }
    }

}
