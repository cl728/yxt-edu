package com.yixuetang.message.schedules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.entity.message.Message;
import com.yixuetang.entity.message.UserMessage;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.user.User;
import com.yixuetang.message.mapper.MessageMapper;
import com.yixuetang.message.mapper.UserMessageMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Colin
 * @version 1.0.0
 * @description 拉取系统通知，存储到 t_user_message 定时任务
 * @date 2020/11/25 10:14
 */
@Component
public class PullSystemMessageScheduleTask {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void task() {
        List<Message> messageList = this.messageMapper.selectList( new QueryWrapper<Message>().eq( "status", Boolean.FALSE ) );
        if (CollectionUtils.isEmpty( messageList )) { // 系统通知表里没有待拉取的系统通知
            return;
        }
        // 将未拉取的系统通知插入到 t_user_message 表里
        messageList.forEach( message -> {
            int receiverType = message.getReceiverType();
            UserMessage insertUserMessage = UserMessage.builder().id( null ).messageId( message.getId() )
                    .status( Boolean.FALSE ).pullTime( new Date() ).build();
            switch (receiverType) {
                case 0: // 该系统通知发布到全体用户
                    this.userMapper.selectList( new QueryWrapper<User>().ne( "role_id", 1L ) )
                            .stream().map( User::getId ).collect( Collectors.toList() )
                            .forEach( userId -> {
                                insertUserMessage.setReceiverId( userId );
                                this.userMessageMapper.insert( insertUserMessage );
                            } );
                    break;
                case 1: // 该系统通知发布到单一用户:
                    insertUserMessage.setReceiverId( message.getReceiverId() );
                    this.userMessageMapper.insert( insertUserMessage );
                    break;
                default:
                    ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
            }
            // 将拉取状态修改为 true
            message.setStatus( Boolean.TRUE );
            this.messageMapper.updateById( message );
        } );
    }

}
