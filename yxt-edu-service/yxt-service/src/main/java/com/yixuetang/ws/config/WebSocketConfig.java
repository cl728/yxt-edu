package com.yixuetang.ws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Colin
 * @version 1.0.0
 * @description WebSocket 配置类
 * @date 2020/11/28 17:03
 */
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注入 ServerEndpointExporter
     * 这个 bean 会自动注册使用了 @ServerEndpoint 注解声明的 Websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
