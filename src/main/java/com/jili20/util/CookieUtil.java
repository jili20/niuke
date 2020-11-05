package com.jili20.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/** 获取 cookie 工具类
 * @author bing  @create 2020/11/5-1:43 下午
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request,String name){
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }
        // 获取到所有的 cookies,一个数组，遍历取出对应名字的 cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
