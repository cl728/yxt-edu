package com.yixuetang.message.controller;

import com.yixuetang.api.message.MessageControllerApi;
import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.request.message.QueryPageRequestMessage;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
