package com.starry.community.configuration;

import com.starry.community.quartz.updateScore.updateScore;
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
@Configuration
public class Quarta_conf {
    @Bean
    public JobDetailFactoryBean updateScoreJobDetail() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(updateScore.class);
        jobDetailFactoryBean.setName("updateScoreJob");
        jobDetailFactoryBean.setGroup("updateScoreJobGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        return jobDetailFactoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean alphas(JobDetail updateScoreJobDetail) {
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        //触发器与Job绑定
        simpleTriggerFactoryBean.setJobDetail(updateScoreJobDetail);

        simpleTriggerFactoryBean.setName("updateScoreTrigger");
        simpleTriggerFactoryBean.setGroup("updateScoreTriggerGroup");
        simpleTriggerFactoryBean.setRepeatInterval(1000 * 60 * 1);
        //存储Job状态的对象
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());

        return simpleTriggerFactoryBean;
    }
}
