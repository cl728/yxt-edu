package com.yixuetang.entity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/1 20:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private ChatUser from;  // 消息推送者

    private String message; // 消息内容

    private ChatUser to; // 消息接收者

    private String time; // 创建时间

    public void setMessage(String message) {
        this.message = message == null ? "" : message.replaceAll( "\r\n|\r|\n", "" );
    }
}
