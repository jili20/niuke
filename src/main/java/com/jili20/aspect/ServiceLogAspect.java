package com.jili20.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 统一记录日志
 *
 * @author bing  @create 2020/11/11-5:00 下午
 */
@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.jili20.service.*.*(..))")
    public void pointcut() { // 切点
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户【1.2.3.4】在[xxx]访问了[com.jili20.service.xxx]
        // 获取请求用户
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost(); // 获取到请求用户的ip
        // 格式化当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 获取类型和目标
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
    }


}
