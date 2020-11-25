package com.yixuetang.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuetang.entity.message.EventRemind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 异步发送事件提醒工具类
 * @date 2020/11/25 16:56
 */
@Component
public class AmqpUtils {

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger( AmqpUtils.class );

    public void sendCourseRemind(long teacherId, long courseId, long sourceId,
                                 String sourceName, String sourceType) {
        EventRemind eventRemind = EventRemind.builder().id( null )
                .remindType( 0 ) // 与课程相关，remindType 为 0
                .senderId( teacherId ) // 发送者为教师
                .courseId( courseId ) // 与之关联的课程
                .receiverId( null ) // 接收者为该课程下的学生，需要在执行异步发送逻辑时查出，此处设置为 null
                .sourceId( sourceId ) // 事件源id
                .sourceType( sourceType )   // 事件源类型
                .sourceName( sourceName )   // 事件源名称
                .url( "http://www.yixuetang.com/courseDetail.html?id=" + courseId ) // 事件所发生的地点链接
                .status( Boolean.FALSE )  // 是否已读，默认为 false
                .remindTime( new Date() )  // 提醒时间
                .build();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String eventRemindJson = mapper.writeValueAsString( eventRemind );
            this.amqpTemplate.convertAndSend( "YXT.REMIND.EXCHANGE", "YXT.REMIND.COURSE", eventRemindJson );
        } catch (JsonProcessingException e) {
            LOGGER.error( "使用 Jackson 解析实体类发生异常！异常原因：{}", e );
        }
    }

}
