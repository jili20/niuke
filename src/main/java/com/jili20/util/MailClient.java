package com.jili20.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/** 提供发送邮件功能
 * @author bing  @create 2020/11/4-11:20 上午
 */
@Component
public class MailClient {
    // 记录日志，以当前类命名
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired // 调用Spring的构建和发送邮箱核心组件
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    // 参数：发给谁，标题，内容
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage(); // 空的模板，需要帮助类往里面添加内容
            MimeMessageHelper helper = new MimeMessageHelper(message); // 帮助构建 message 里的内容
            helper.setFrom(from); // 设置发件人
            helper.setTo(to); // 设置收件人
            helper.setSubject(subject); // 邮件标题
            helper.setText(content, true); // 邮件内容
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage()); // 记录日志
        }
    }
}
