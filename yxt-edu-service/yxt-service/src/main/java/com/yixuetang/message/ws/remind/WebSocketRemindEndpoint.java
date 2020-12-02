package com.yixuetang.message.ws.remind;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yixuetang.entity.response.result.UnreadCountResp;
import com.yixuetang.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/message/{userId}/unreadCount")
public class WebSocketRemindEndpoint {

    @Autowired
    private MessageService messageService;

    private Session session;

    private static CopyOnWriteArraySet<WebSocketRemindEndpoint> webSockets = new CopyOnWriteArraySet<>();
    private static Map<String, Session> sessionPool = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger( WebSocketRemindEndpoint.class );

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        this.session = session;
        webSockets.add( this );
        sessionPool.put( userId, session );
        LOGGER.info( userId + "【websocket消息】有新的连接，总数为: {}", webSockets.size() );
    }

    @OnClose
    public void onClose() {
        webSockets.remove( this );
        LOGGER.info( "【websocket消息】连接断开，总数为: {}", webSockets.size() );
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info( "【websocket消息】收到客户端消息: {}", message );
    }

    public void sendUnreadCountToUser(Long userId) {

        Session session = sessionPool.get( String.valueOf( userId ) );
        if (session != null) {
            try {
                UnreadCountResp unreadCountResp =
                        (UnreadCountResp) this.messageService.findUnreadCountByUserId( userId )
                                .getQueryResult().getData().get( 0 );

                ObjectMapper objectMapper = new ObjectMapper();
                String unreadCountRespStr = objectMapper.writeValueAsString( unreadCountResp );
                session.getAsyncRemote().sendText( unreadCountRespStr );
                LOGGER.info( " ws 发送消息成功！unreadResp: {}", unreadCountResp );
            } catch (Exception e) {
                LOGGER.error( "使用 Jackson 解析实体类发生异常！异常原因：{}", e );
            }
        }

    }
}