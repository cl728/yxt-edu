package com.yixuetang.task.config;

import com.yixuetang.task.LikeTask;
import com.yixuetang.task.PullSystemMessageTask;
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

    private static final String PULL_SYSTEM_MESSAGE_TASK_IDENTITY = "PullSystemMessageTaskQuartz";

    @Bean(name = "quartzDetail1")
    public JobDetail quartzDetail1() {
        return JobBuilder.newJob( LikeTask.class ).withIdentity( LIKE_TASK_IDENTITY ).storeDurably().build();
    }

    @Bean(name = "quartzTrigger1")
    public Trigger quartzTrigger1() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours( 2 )  // 两个小时执行一次
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob( quartzDetail1() )
                .withIdentity( LIKE_TASK_IDENTITY )
                .withSchedule( scheduleBuilder )
                .build();
    }

    @Bean(name = "quartzDetail2")
    public JobDetail quartzDetail2() {
        return JobBuilder.newJob( PullSystemMessageTask.class ).withIdentity( PULL_SYSTEM_MESSAGE_TASK_IDENTITY ).storeDurably().build();
    }

    @Bean(name = "quartzTrigger2")
    public Trigger quartzTrigger2() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours( 3 )  // 三个小时执行一次
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob( quartzDetail2() )
                .withIdentity( PULL_SYSTEM_MESSAGE_TASK_IDENTITY )
                .withSchedule( scheduleBuilder )
                .build();
    }
}
