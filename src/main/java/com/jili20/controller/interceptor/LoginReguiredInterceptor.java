package com.jili20.controller.interceptor;

import com.jili20.annotation.LoginRequired;
import com.jili20.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/** 拦截器
 * @author bing  @create 2020/11/6-4:08 下午
 */
@Component
public class LoginReguiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断拦截的目标是不是一个方法，HandlerMethod MVC 提供的类型
        if (handler instanceof HandlerMethod) {
            // 为了方便获取内容，转型
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取拦截到的对象
            Method method = handlerMethod.getMethod();
            // 尝试从方法对象中去取注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (loginRequired !=null && hostHolder.getUser() == null) {
                // 如果没登录，重定向到登录页面
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
