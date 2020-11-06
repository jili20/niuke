package com.jili20.config;

import com.jili20.controller.interceptor.LoginReguiredInterceptor;
import com.jili20.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 注册拦截器配置类
 * @author bing  @create 2020/11/5-12:05 下午
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginReguiredInterceptor loginReguiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 排除所有静态资源，所有请求都要处理
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        // 不要处理静态资源，动态资源都处理，但人为的筛选了带有自定义注解@LoginRequired的方法
        registry.addInterceptor(loginReguiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
