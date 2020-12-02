package com.yixuetang.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuetang.entity.message.ChatMessage;
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
                                 String sourceName, String action, String url) {
        EventRemind eventRemind = EventRemind.builder().id( null )
                .remindType( 0 ) // 与课程相关，remindType 为 0
                .senderId( teacherId ) // 发送者为教师
                .courseId( courseId ) // 与之关联的课程
                .receiverId( null ) // 接收者为该课程下的学生，需要在执行异步发送逻辑时查出，此处设置为 null
                .sourceId( sourceId ) // 事件源id
                .action( action )   // 动作类型
                .sourceName( sourceName )   // 事件源名称
                .url( url ) // 事件所发生的地点链接
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

    public void sendReplyRemind(long userId, long receiverId, long commentId, long targetId, String url) {
        EventRemind eventRemind = EventRemind.builder().id( null )
                .remindType( 1 ) // 与回复相关，remindType 为 1
                .senderId( userId ) // 发送者为评论的人
                .courseId( null ) // 与之关联的课程，这里为 null
                .receiverId( receiverId ) // 接收者为被评论的人
                .sourceId( commentId ) // 事件源id
                .targetId( targetId ) // 目标id，这里是被回复的评论id
                .action( "回复" )   // 动作类型，这里是回复
                .sourceName( "评论" )   // 事件源名称，这里是评论
                .url( url ) // 事件所发生的地点链接
                .status( Boolean.FALSE )  // 是否已读，默认为 false
                .remindTime( new Date() )  // 提醒时间
                .build();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String eventRemindJson = mapper.writeValueAsString( eventRemind );
            this.amqpTemplate.convertAndSend( "YXT.REMIND.EXCHANGE", "YXT.REMIND.REPLY", eventRemindJson );
        } catch (JsonProcessingException e) {
            LOGGER.error( "使用 Jackson 解析实体类发生异常！异常原因：{}", e );
        }
    }

    public void sendChatRemind(ChatMessage chatMessage) {
        EventRemind eventRemind = EventRemind.builder().id( null )
                .remindType( 3 ) // 与私信相关，remindType 为 3
                .senderId( chatMessage.getFrom().getId() ) // 发送者
                .courseId( null ) // 与之关联的课程，这里为 null
                .receiverId( chatMessage.getTo().getId() ) // 接收者
                .sourceId( null ) // 事件源id，这里为 null
                .message( chatMessage.getMessage() )    // 发送的私信内容
                .targetId( null ) // 目标id，这里为 null
                .action( "发送" )   // 动作类型，这里是发送
                .sourceName( "私信" )   // 事件源名称，这里是私信
                .url( "http://www.yixuetang.com/message.html" ) // 事件所发生的地点链接
                .status( Boolean.FALSE )  // 是否已读，默认为 false
                .remindTime( new Date() )  // 提醒时间
                .build();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String eventRemindJson = mapper.writeValueAsString( eventRemind );
            this.amqpTemplate.convertAndSend( "YXT.REMIND.EXCHANGE", "YXT.REMIND.CHAT", eventRemindJson );
        } catch (JsonProcessingException e) {
            LOGGER.error( "使用 Jackson 解析实体类发生异常！异常原因：{}", e );
        }
    }

}
