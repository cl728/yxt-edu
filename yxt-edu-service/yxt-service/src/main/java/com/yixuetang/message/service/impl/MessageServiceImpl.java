package com.yixuetang.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.entity.message.Message;
import com.yixuetang.entity.message.UserMessage;
import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.request.message.QueryPageRequestMessage;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.Role;
import com.yixuetang.entity.user.User;
import com.yixuetang.message.mapper.MessageMapper;
import com.yixuetang.message.mapper.UserMessageMapper;
import com.yixuetang.message.mapper.UserMessageSettingMapper;
import com.yixuetang.message.service.MessageService;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Colin
 * @version 1.0.0
 * @description 消息模块服务层接口实现类
 * @date 2020/11/23 10:42
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMessageSettingMapper messageSettingMapper;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    @Transactional
    public CommonResponse updateMessageSetting(Long userId, UserMessageSetting setting) {

        if (this.userMapper.selectOne(
                new QueryWrapper<User>().eq( "id", userId ).select( "id" ) ) == null
                || ObjectUtils.isEmpty( setting )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        Long id = this.messageSettingMapper.selectOne(
                new QueryWrapper<UserMessageSetting>().eq( "user_id", userId ).select( "id" ) ).getId();

        this.messageSettingMapper.updateById( UserMessageSetting.builder()
                .id( id ).userId( userId ).allRemind( setting.getAllRemind() )
                .course( setting.getCourse() ).reply( setting.getReply() )
                .voteUp( setting.getVoteUp() ).chat( setting.getChat() ).build() );

        return CommonResponse.SUCCESS();

    }

    @Override
    @Transactional
    public QueryResponse findMessageSettingByUserId(Long userId) {

        if (this.userMapper.selectOne(
                new QueryWrapper<User>().eq( "id", userId ).select( "id" ) ) == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        UserMessageSetting userMessageSetting = this.messageSettingMapper.selectOne(
                new QueryWrapper<UserMessageSetting>().eq( "user_id", userId ) );

        // 如果用户第一次查询消息提醒设置，则默认全为 true
        // 并且将该记录插入到用户消息提醒表里
        UserMessageSetting setting = UserMessageSetting.builder().userId( userId )
                .allRemind( true ).course( true ).reply( true ).voteUp( true ).chat( true ).build();

        if (ObjectUtils.isEmpty( userMessageSetting )) {    // 消息提醒表里没有相关记录
            this.messageSettingMapper.insert( setting );
            return new QueryResponse( CommonCode.SUCCESS,
                    new QueryResult<>( Collections.singletonList( setting ), 1 ) );
        }

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( Collections.singletonList( userMessageSetting ), 1 ) );
    }

    @Override
    public QueryResponse findMessageListByUserId(Long userId) {
        if (!ObjectUtils.allNotNull( userId,
                this.userMapper.selectOne( new QueryWrapper<User>().eq( "id", userId ) ) )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        List<Message> messageList = new ArrayList<>();
        this.userMessageMapper
                .selectList( new QueryWrapper<UserMessage>().eq( "receiver_id", userId ) )
                .stream()
                .map( UserMessage::getMessageId )
                .collect( Collectors.toList() )
                .forEach( messageId -> messageList.add( this.messageMapper.selectById( messageId ) ) );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( messageList, messageList.size() ) );
    }

    @Override
    public QueryResponse findMessageListByPage(long currentPage, long pageSize, QueryPageRequestMessage queryPageRequestMessage) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();

        this.screen( queryPageRequestMessage, queryWrapper );

        List<Message> messageList = this.messageMapper.findByPage( new Page<>( currentPage, pageSize ), queryPageRequestMessage );

        // 如果接收者 id 不为 0， 此通知是发送给单一用户
        // 需要将其用户名查出
        messageList.forEach( message -> {
            if (message.getReceiverId() != 0) {
                message.setReceiver(
                        this.userMapper.selectOne( new QueryWrapper<User>().eq( "id", message.getReceiverId() )
                                .select( "id", "username" ) ) );
            }
        } );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( messageList, this.messageMapper.selectCount( queryWrapper ) ) );
    }

    @Override
    public QueryResponse findMessageById(long messageId) {
        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( Collections.singletonList(
                        this.messageMapper.selectById( messageId ) ), 1 ) );
    }

    private void screen(QueryPageRequestMessage queryPageRequestMessage, QueryWrapper<Message> queryWrapper) {
        String receiverType = queryPageRequestMessage.getReceiverType();
        if (StringUtils.isNoneBlank( receiverType )) {
            int type = StringUtils.equals( "全部用户", receiverType ) ? 0
                    : StringUtils.equals( "单一用户", receiverType ) ? 1 : -1;
            queryPageRequestMessage.setType( type );
            queryWrapper.eq( "receiver_type", type );
        }

        String queryStatus = queryPageRequestMessage.getStatus();
        if (StringUtils.isNoneBlank( queryStatus )) {
            Boolean status = StringUtils.equals( "已拉取", queryStatus ) ? Boolean.TRUE
                    : StringUtils.equals( "未拉取", queryStatus ) ? Boolean.FALSE : null;
            queryPageRequestMessage.setMessageStatus( status );
            queryWrapper.eq( "status", status );
        }

        String title = queryPageRequestMessage.getTitle();
        if (StringUtils.isNoneBlank( title )) {
            queryPageRequestMessage.setTitle( "%" + title + "%" );
            queryWrapper.like( "title", queryPageRequestMessage.getTitle() );
        }
    }
}
