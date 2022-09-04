package com.starry.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

/**
 * @author Starry
 * @create 2022-09-03-1:16 PM
 * @Describe    发送邮件的工具类，用STMP协议委托腾讯发送
 */
@Component
public class EmailSender {
    //日志记录类
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    //由于引入了相关starter，因此可以自动注入
    @Autowired
    private JavaMailSender javaMailSender;

    //配置文件与field绑定的注解，读取配置文件中指定key的value，绑定到field中
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     * @param to 目标邮箱
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content) {
        try {
            //创建邮件
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            //利用工具类填充邮件信息
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setText(content,true);
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            logger.error("发送邮件失败:" + e.getMessage());
        }
    }

}
