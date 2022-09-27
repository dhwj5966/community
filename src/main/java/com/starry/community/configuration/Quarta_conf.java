package com.starry.community.configuration;

import com.starry.community.quartz.AlphaJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author Starry
 * @create 2022-09-27-10:45 AM
 * @Describe 该配置只有创建容器的时候有用。之后Quartz会访问数据库而不是配置文件
 */
//@Configuration
//public class Quarta_conf {
//
//    @Bean
//    public JobDetailFactoryBean alphaj() {
//        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
//        jobDetailFactoryBean.setJobClass(AlphaJob.class);
//        jobDetailFactoryBean.setName("alphaJob");
//        jobDetailFactoryBean.setGroup("alphaJobGroup");
//        jobDetailFactoryBean.setDurability(true);
//        jobDetailFactoryBean.setRequestsRecovery(true);
//        return jobDetailFactoryBean;
//    }
//
//    @Bean
//    public SimpleTriggerFactoryBean alphas(JobDetail alphaj) {
//        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
//        //触发器与Job绑定
//        simpleTriggerFactoryBean.setJobDetail(alphaj);
//
//        simpleTriggerFactoryBean.setName("alphaTrigger");
//        simpleTriggerFactoryBean.setGroup("alphaTriggerGroup");
//        simpleTriggerFactoryBean.setRepeatInterval(3000);
//        //存储Job状态的对象
//        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());
//
//        return simpleTriggerFactoryBean;
//    }
//}
