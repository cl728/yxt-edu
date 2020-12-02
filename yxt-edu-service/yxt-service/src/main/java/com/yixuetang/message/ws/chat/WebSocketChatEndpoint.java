package com.yixuetang.message.ws.chat;

import com.alibaba.fastjson.JSONObject;
import com.yixuetang.entity.message.ChatMessage;
import com.yixuetang.message.ws.chat.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/1 20:25
 */
@Component
@ServerEndpoint("/chat/{userId}")
public class WebSocketChatEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger( WebSocketChatEndpoint.class );

    private static ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService) {
        WebSocketChatEndpoint.chatService = chatService;
    }

    // 用于存放当前 Websocket 对象的 Set 集合
    private static CopyOnWriteArraySet<WebSocketChatEndpoint> websocketServerEndpoints = new CopyOnWriteArraySet<>();

    private static Map<String, Session> sessionPool = new HashMap<>();

    // 与客户端的会话 Session
    private Session session;

    // 当前会话用户ID
    private String fromId = "";

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String id) {
        LOGGER.info( "onOpen >> 链接成功" );
        this.session = session;

        // 将当前websocket对象存入到Set集合中
        websocketServerEndpoints.add( this );

        sessionPool.put( id, session );

        LOGGER.info( "有新用户开始监听：" + id );

        this.fromId = id;

    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        LOGGER.info( "onClose >> 链接关闭" );

        // 移除当前 Websocket 对象
        websocketServerEndpoints.remove( this );

    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        LOGGER.info( "接收到用户：" + fromId + " 的信息：" + message );
    }

    @OnError
    public void onError(Throwable e) {
        LOGGER.error( "链接出现异常：{}", e );
    }

    /**
     * 推送消息
     */
    private void sendChatMessage(String userId, String chatUser) throws IOException {
        Session session = sessionPool.get( userId );
        if (!ObjectUtils.isEmpty( session )) {
            session.getBasicRemote().sendText( chatUser );
        }
    }

    /**
     * 指定用户推送消息
     *
     * @param chatMessage 推送消息
     * @param toId        接收方ID
     */
    public void sendTo(String toId, ChatMessage chatMessage) {
        fromId = chatMessage.getFrom().getId().toString();
        try {
            LOGGER.info( chatMessage.getFrom().getId() + " 推送消息到用户：" + toId + " ，推送内容：" + chatMessage.getMessage() );
            chatService.pushChatMessage( fromId, toId, chatMessage.getMessage(),
                    ObjectUtils.isEmpty( sessionPool.get( toId ) ) );
            this.sendChatMessage( toId, JSONObject.toJSONString( chatService.findById( fromId ) ) );
        } catch (Exception e) {
            LOGGER.error( "推送消息发生异常！异常原因：{}", e );
        }
    }

}
