package com.yixuetang.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.message.mapper.UserMessageSettingMapper;
import com.yixuetang.message.service.MessageService;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

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
                .reply( setting.getReply() ).voteUp( setting.getVoteUp() )
                .chat( setting.getChat() ).build() );

        return CommonResponse.SUCCESS();

    }

    @Override
    @Transactional
    public QueryResponse findByUserId(Long userId) {

        if (this.userMapper.selectOne(
                new QueryWrapper<User>().eq( "id", userId ).select( "id" ) ) == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        UserMessageSetting userMessageSetting = this.messageSettingMapper.selectOne(
                new QueryWrapper<UserMessageSetting>().eq( "user_id", userId ) );

        UserMessageSetting setting = UserMessageSetting.builder().userId( userId ).allRemind( true )
                .reply( true ).voteUp( true ).chat( true ).build();

        if (ObjectUtils.isEmpty( userMessageSetting )) {    // 消息提醒表里没有相关记录
            this.messageSettingMapper.insert( setting );
            return new QueryResponse( CommonCode.SUCCESS,
                    new QueryResult<>( Collections.singletonList( setting ), 1 ) );
        }

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( Collections.singletonList( userMessageSetting ), 1 ) );
    }
}
