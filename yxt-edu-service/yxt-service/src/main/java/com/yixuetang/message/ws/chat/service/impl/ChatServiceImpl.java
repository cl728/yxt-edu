package com.yixuetang.message.ws.chat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yixuetang.entity.message.ChatMessage;
import com.yixuetang.entity.message.ChatUser;
import com.yixuetang.entity.message.CommonConstant;
import com.yixuetang.entity.user.User;
import com.yixuetang.message.ws.chat.service.ChatService;
import com.yixuetang.mq.AmqpUtils;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.message.ChatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/1 20:29
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpUtils amqpUtils;

    private static final Logger LOGGER = LoggerFactory.getLogger( ChatServiceImpl.class );

    @Override
    public ChatUser findById(String id) {
        User user = this.userMapper.selectById( Long.parseLong( id ) );
        if (!ObjectUtils.isEmpty( user )) {
            return ChatUser.builder()
                    .id( user.getId() )
                    .name( user.getUsername() )
                    .avatar( user.getAvatar() )
                    .build();
        }
        LOGGER.error( "查询不到指定用户id的信息，用户id为：{}", id );
        return null;
    }

    @Override
    public void pushChatMessage(String fromId, String toId, String message, Boolean sendRemind) {
        ChatMessage chatMessage = ChatMessage.builder()
                .message( message )
                .from( this.findById( fromId ) )
                .to( this.findById( toId ) )
                .time( ChatUtils.format( new Date() ) )
                .build();
        // 推送消息
        this.push( chatMessage, CommonConstant.CHAT_FROM_PREFIX + fromId + CommonConstant.CHAT_TO_PREFIX + toId );
        // 发送事件提醒
        if (sendRemind) {
            this.amqpUtils.sendChatRemind( chatMessage );
        }
    }

    /**
     * 推送消息
     *
     * @param entity Session value
     * @param key    Session key
     */
    private void push(ChatMessage entity, String key) {
        // 这里按照 PREFIX_ID 格式，作为 KEY 储存消息记录
        // 但一个用户可能推送很多消息，VALUE 应该是数组
        List<ChatMessage> list = new ArrayList<>();
        String value = redisTemplate.boundValueOps( key ).get();
        if (value == null) {
            // 第一次推送消息
            list.add( entity );
        } else {
            // 第n次推送消息
            list = Objects.requireNonNull( JSONObject.parseArray( value ) ).toJavaList( ChatMessage.class );
            list.add( entity );
        }
        redisTemplate.boundValueOps( key ).set( JSONObject.toJSONString( list ) );
    }

    @Override
    public List<ChatMessage> selfList(String fromId, String toId) {
        List<ChatMessage> list = new ArrayList<>();
        // A -> B
        String fromTo = redisTemplate.boundValueOps( CommonConstant.CHAT_FROM_PREFIX + fromId + CommonConstant.CHAT_TO_PREFIX + toId ).get();
        // B -> A
        String toFrom = redisTemplate.boundValueOps( CommonConstant.CHAT_FROM_PREFIX + toId + CommonConstant.CHAT_TO_PREFIX + fromId ).get();

        JSONArray fromToObject = JSONObject.parseArray( fromTo );
        JSONArray toFromObject = JSONObject.parseArray( toFrom );
        if (!ObjectUtils.isEmpty( fromToObject )) {
            list.addAll( fromToObject.toJavaList( ChatMessage.class ) );
        }
        if (!ObjectUtils.isEmpty( toFromObject )) {
            list.addAll( toFromObject.toJavaList( ChatMessage.class ) );
        }

        if (!CollectionUtils.isEmpty( list )) {
            ChatUtils.sort( list );
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<ChatUser> latelyList(String userId) {
        Set<ChatUser> set = new HashSet<>();
        Set<String> fromSelfKeys = redisTemplate.keys( CommonConstant.CHAT_FROM_PREFIX + userId + CommonConstant.REDIS_MATCH_PREFIX );
        if (!CollectionUtils.isEmpty( fromSelfKeys )) {
            fromSelfKeys.forEach( key -> {
                key = key.substring( key.lastIndexOf( "_" ) + 1 );
                set.add( this.findById( key ) );
            } );
        }
        Set<String> toSelfKeys = redisTemplate.keys( CommonConstant.REDIS_MATCH_PREFIX + CommonConstant.CHAT_TO_PREFIX + userId );
        if (!CollectionUtils.isEmpty( toSelfKeys )) {
            toSelfKeys.forEach( key -> {
                key = key.split( "_" )[2];
                set.add( this.findById( key ) );
            } );
        }
        return new ArrayList<>( set );
    }

}
