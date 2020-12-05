package com.yixuetang.task;

import com.yixuetang.comment.service.CommentService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author Colin
 * @version 1.0.0
 * @description 点赞的定时任务
 * @date 2020/12/5 21:12
 */
@Component
public class LikeTask extends QuartzJobBean {

    @Autowired
    private CommentService commentService;

    private static final Logger LOGGER = LoggerFactory.getLogger( LikeTask.class );

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info( "--------LikeTask--------" );

        // 将 Redis 里的点赞信息同步到数据库里
        this.commentService.transLikedFromRedis2DB();
    }
}
