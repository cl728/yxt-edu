package com.yixuetang.message.controller;

import com.yixuetang.api.message.MessageControllerApi;
import com.yixuetang.entity.message.ChatMessage;
import com.yixuetang.entity.message.ChatUser;
import com.yixuetang.entity.message.Message;
import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.request.message.QueryPageRequestMessage;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.message.service.MessageService;
import com.yixuetang.message.ws.chat.WebSocketChatEndpoint;
import com.yixuetang.message.ws.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 消息模块控制层
 * @date 2020/11/23 10:39
 */
@RestController
@RequestMapping("message")
public class MessageController implements MessageControllerApi {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @Override
    @DeleteMapping("eventRemind/remindId/{remindId}/userId/{userId}")
    public CommonResponse deleteRemindById(@PathVariable Long remindId, @PathVariable Long userId) {
        return this.messageService.deleteRemindById( remindId, userId );
    }

    @Override
    @DeleteMapping("admin/messageId/{messageId}")
    public CommonResponse deleteMessageById(@PathVariable Long messageId) {
        return this.messageService.deleteMessageById( messageId );
    }

    @Override
    @PutMapping("setting/userId/{userId}")
    public CommonResponse updateMessageSetting(@PathVariable Long userId,
                                               @RequestBody UserMessageSetting setting) {
        return this.messageService.updateMessageSetting( userId, setting );
    }

    @Override
    @GetMapping("setting/userId/{userId}")
    public QueryResponse findMessageSettingByUserId(@PathVariable Long userId) {
        return this.messageService.findMessageSettingByUserId( userId );
    }

    @Override
    @GetMapping("userId/{userId}")
    public QueryResponse findMessageListByUserId(@PathVariable Long userId) {
        return this.messageService.findMessageListByUserId( userId );
    }

    @Override
    @GetMapping("page/{currentPage}/{pageSize}")
    public QueryResponse findMessageListByPage(@PathVariable long currentPage,
                                               @PathVariable long pageSize,
                                               QueryPageRequestMessage queryPageRequestMessage) {
        return this.messageService.findMessageListByPage( currentPage, pageSize, queryPageRequestMessage );
    }

    @Override
    @GetMapping("info/messageId/{messageId}")
    public QueryResponse findMessageById(@PathVariable long messageId) {
        return this.messageService.findMessageById( messageId );
    }

    @Override
    @PostMapping("admin/adminId/{adminId}")
    public CommonResponse inputMessage(@PathVariable long adminId, @RequestBody Message message) {
        return this.messageService.inputMessage( adminId, message );
    }

    @Override
    @PutMapping("admin/adminId/{adminId}/messageId/{messageId}")
    public CommonResponse editMessage(@PathVariable long adminId,
                                      @PathVariable long messageId,
                                      @RequestBody Message message) {
        return this.messageService.editMessage( adminId, messageId, message );
    }

    @Override
    @GetMapping("unreadCount/userId/{userId}")
    public QueryResponse findUnreadCountByUserId(@PathVariable long userId) {
        return this.messageService.findUnreadCountByUserId( userId );
    }

    @Override
    @GetMapping("eventRemind/userId/{userId}")
    public QueryResponse findEventRemindListByUserId(@PathVariable long userId) {
        return this.messageService.findEventRemindListByUserId( userId );
    }

    @Override
    @PostMapping("chat/toId/{toId}")
    public CommonResponse push(@PathVariable long toId, @RequestBody ChatMessage message) {
        WebSocketChatEndpoint endpoint = new WebSocketChatEndpoint();
        endpoint.sendTo( String.valueOf( toId ), message );
        return CommonResponse.SUCCESS();
    }

    @Override
    @GetMapping("chat/self/fromId/{fromId}/toId/{toId}")
    public QueryResponse findSelfMessageList(@PathVariable("fromId") long fromId,
                                             @PathVariable("toId") long toId) {
        List<ChatMessage> chatMessages = this.chatService.selfList( String.valueOf( fromId ), String.valueOf( toId ) );
        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( chatMessages, chatMessages.size() ) );
    }

    @Override
    @GetMapping("chat/lately/userId/{userId}")
    public QueryResponse findLatelyUserList(@PathVariable long userId) {
        List<ChatUser> chatUsers = this.chatService.latelyList( String.valueOf( userId ) );
        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( chatUsers, chatUsers.size() ) );
    }
}
