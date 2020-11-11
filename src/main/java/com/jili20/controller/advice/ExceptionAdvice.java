package com.jili20.controller.advice;


import com.jili20.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author bing  @create 2020/11/11-3:47 下午
 */
// 扫描所有带有 Controller 注解的 Bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        // 通过请求头，判断是否异步请求
        String xRequestWith = request.getHeader("x-requested-with");
        // 如果是异步请求
        if ("XMLHttpRequest".equals(xRequestWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常"));
        } else {
            // 非异步请求,重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
