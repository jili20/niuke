package com.jili20.controller.interceptor;

import com.jili20.entity.LoginTicket;
import com.jili20.entity.User;
import com.jili20.service.UserService;
import com.jili20.util.CookieUtil;
import com.jili20.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 拦截器
 *
 * @author bing  @create 2020/11/5-1:23 下午
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    // 在Controller之前执行
    // 通过凭证找到用户，把用户暂存到 hostHolder 线程对应的对象里。
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 cookie 中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        // 如果非空，表示已经登录
        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);//得到对象
            // 检查凭证是否有效:非空，状态=0，超时时间晚于当前时间（当前时间之后）
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 凭证有效，根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }


    // 在Controller之后执行，在模板引擎调用之前，把上面的user对象存进去
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 把上面存的对象（登录用户）取出来，非空，加入模板
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }


    // 在TemplateEngine执行之后，清理登录用户
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
