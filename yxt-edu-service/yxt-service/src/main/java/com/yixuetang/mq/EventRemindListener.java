package com.yixuetang.mq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.entity.course.StudentCourse;
import com.yixuetang.entity.message.EventRemind;
import com.yixuetang.message.mapper.RemindMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * @author Colin
 * @version 1.0.0
 * @description 消息队列，异步发送事件提醒
 * @date 2020/11/24 16:29
 */
@Component
public class EventRemindListener {

    private static final Logger LOGGER = LoggerFactory.getLogger( EventRemindListener.class );

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private ScMapper scMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "YXT.REMIND.QUEUE", durable = "true"),
            exchange = @Exchange(value = "YXT.REMIND.EXCHANGE", ignoreDeclarationExceptions = "true"),
            key = {"YXT.REMIND.COURSE", "YXT.REMIND.REPLY", "YXT.REMIND.VOTEUP", "YXT.REMIND.CHAT"}
    ))
    @Transactional
    public void sendEventRemind(String eventRemindJson) {

        ObjectMapper mapper = new ObjectMapper();
        EventRemind eventRemind;
        try {
            eventRemind = mapper.readValue( eventRemindJson, EventRemind.class );
        } catch (JsonProcessingException e) {
            LOGGER.error( "使用 Jackson 将字符串还原成实体类对象发生异常！异常原因：{}", e );
            return;
        }

        if (ObjectUtils.isEmpty( eventRemind )) {
            LOGGER.error( "事件提醒发送失败！失败原因：所要发送的事件提醒为空！" );
            return;
        }

        // 与课程相关并有课程id与之关联的事件提醒，接收者为该课程下的学生
        Long courseId = eventRemind.getCourseId();
        if (eventRemind.getRemindType() == 0 && ObjectUtils.isNotEmpty( courseId )) {
            this.scMapper.selectList(
                    new QueryWrapper<StudentCourse>().eq( "course_id", courseId ) )
                    .stream()
                    .map( StudentCourse::getStudentId )
                    .collect( Collectors.toList() )
                    .forEach( studentId -> {
                        eventRemind.setReceiverId( studentId );
                        this.remindMapper.insert( eventRemind );
                    } );
        } else {
            this.remindMapper.insert( eventRemind ); // 否则为传送过来的接收者id
        }


    }

}
