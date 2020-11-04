package com.jili20.controller;

import com.jili20.entity.User;
import com.jili20.service.UserService;
import com.jili20.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author bing  @create 2020/11/4-12:18 下午
 */
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/"); // 8 秒后跳到首页
            return "/site/operate-result"; // 操作结果页面
        }else {
            model.addAttribute("usernamMsg",map.get("usernamMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 处理激活状态
    // http://localhost:8080/community/activation/101/code
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model,@PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "注册成功，您的账号可以正常使用了！");
            model.addAttribute("target", "/login"); // 8 秒后跳到首页
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作，该账号已经激活过了！");
            model.addAttribute("target", "/"); // 8 秒后跳到首页
        }else {
            model.addAttribute("msg", "激活失败，你提供的激活码不正确！");
            model.addAttribute("target", "/"); // 8 秒后跳到首页
        }
        return "/site/operate-result"; // 操作结果页面

    }



}
