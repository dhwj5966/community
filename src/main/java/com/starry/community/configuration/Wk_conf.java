package com.starry.community.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author Starry
 * @create 2022-09-27-8:57 PM
 * @Describe
 */
@Configuration
public class Wk_conf {

    private static final Logger logger = LoggerFactory.getLogger(Wk_conf.class);

    @Value(value = "${wk.image.command}")
    private String command;

    @Value(value = "${wk.image.storage}")
    private String storage;

    @PostConstruct//表示在初始化之后执行该方法
    public void init() {
        //创建WK图片的存放目录
        File file = new File(storage);
        if (!file.exists()) {
            file.mkdirs();
            logger.info("创建wk目录" + storage);
        }

    }

}
