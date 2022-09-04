package com.starry.community;

import com.starry.community.util.EmailSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Starry
 * @create 2022-09-03-11:19 AM
 * @Describe
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {
    public static Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void test2() {
        //创造要发送的数据
        //1.new 一个 Context对象
        Context context = new Context();
        //2.把要存的数据存进去
        context.setVariable("username","wzh");
        //3.获取模板引擎字符串
        String process = templateEngine.process("/mail/activation.html", context);
        //发送
        emailSender.sendMail("2904650741@qq.com","测试",process);
    }

    @Test
    public void test() {
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }

    @Test
    public void test1() {
        emailSender.sendMail("2904650741@qq.com","测试","是否收到，你好！sad");
    }

}
