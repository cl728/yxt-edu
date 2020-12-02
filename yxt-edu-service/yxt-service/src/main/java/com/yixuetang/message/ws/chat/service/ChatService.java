package com.yixuetang.message.ws.chat.service;

import com.yixuetang.entity.message.ChatMessage;
import com.yixuetang.entity.message.ChatUser;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/1 20:28
 */
public interface ChatService {

    /**
     * 根据用户id查询聊天用户信息
     *
     * @param id 用户id
     * @return ChatUser对象
     */
    ChatUser findById(String id);

    /**
     * 推送消息，储存到Redis数据库中
     *
     * @param fromId     推送方ID
     * @param toId       接收方ID
     * @param message    消息
     * @param sendRemind 是否向接收者发送私信提醒
     */
    void pushChatMessage(String fromId, String toId, String message, Boolean sendRemind);


    /**
     * 获取该用户与指定用户的推送消息
     *
     * @param fromId 推送方ID
     * @param toId   接收方ID
     * @return 该用户与指定用户的推送消息列表
     */
    List<ChatMessage> selfList(String fromId, String toId);

    /**
     * 获取某个用户的最近聊天用户列表
     *
     * @param userId 用户id
     * @return 该用户的最近用户聊天列表
     */
    List<ChatUser> latelyList(String userId);
}
