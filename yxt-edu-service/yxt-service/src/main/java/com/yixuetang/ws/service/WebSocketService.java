package com.yixuetang.ws.service;

/**
 * @author Colin
 * @version 1.0.0
 * @description WebSocket 服务层接口
 * @date 2020/11/28 21:08
 */
public interface WebSocketService {

    /**
     * 订阅链接为 /message/{userId}/unreadCount 的用户能收到消息
     *
     * @param userId 接收消息的用户id
     */
    void sendUnreadCountToUser(Long userId);

}
