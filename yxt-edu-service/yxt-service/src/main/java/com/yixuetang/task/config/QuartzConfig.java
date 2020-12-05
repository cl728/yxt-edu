package com.yixuetang.task.config;

import com.yixuetang.task.LikeTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/5 21:10
 */
@Configuration
public class QuartzConfig {

    private static final String LIKE_TASK_IDENTITY = "LikeTaskQuartz";

    @Bean
    public JobDetail quartzDetail() {
        return JobBuilder.newJob( LikeTask.class ).withIdentity( LIKE_TASK_IDENTITY ).storeDurably().build();
    }

    @Bean
    public Trigger quartzTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours( 2 )  // 两个小时执行一次
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob( quartzDetail() )
                .withIdentity( LIKE_TASK_IDENTITY )
                .withSchedule( scheduleBuilder )
                .build();
    }
}
