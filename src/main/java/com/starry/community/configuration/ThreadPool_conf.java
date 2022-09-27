package com.starry.community.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Starry
 * @create 2022-09-27-9:58 AM
 * @Describe
 */
@Configuration
@EnableScheduling//启动定时任务
@EnableAsync//
public class ThreadPool_conf {



}
