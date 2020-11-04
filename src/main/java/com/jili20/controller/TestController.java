package com.jili20.controller;

import com.jili20.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author bing  @create 2020/11/4-5:32 下午
 */
@RequestMapping("/test")
@Controller
public class TestController {

    // 存cookid
    // http://jili20.com/test/cookie/set
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        cookie.setPath("/test"); // 生效范围
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "set cookie";
    }

    // 获取指定名字的cookie
    // http://jili20.com/test/cookie/get
    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code); // 需 cookie 没过期
        return "get cookie";
    }



    // session 安全，但服务器有压力

    // 设置session示例
    // http://jili20.com/test/session/set
    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }


    // 取出 session示例
    // http://jili20.com/test/session/get
    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

}
