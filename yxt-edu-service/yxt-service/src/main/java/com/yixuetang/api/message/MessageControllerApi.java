package com.yixuetang.api.message;

import com.yixuetang.entity.message.ChatMessage;
import com.yixuetang.entity.message.Message;
import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.request.message.QueryPageRequestMessage;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/23 10:34
 */
@Api(value = "站内消息模块接口", description = "站内消息模块接口，提供站内消息服务接口")
public interface MessageControllerApi {

    @ApiOperation("用户删除某一事件提醒")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remindId", value = "事件提醒id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse deleteRemindById(Long remindId, Long userId);

    @ApiOperation("管理员删除系统通知")
    @ApiImplicitParam(name = "messageId", value = "通知id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse deleteMessageById(Long messageId);

    @ApiOperation("修改用户消息提醒设置")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updateMessageSetting(Long userId, UserMessageSetting setting);

    @ApiOperation("查询某个用户的消息提醒设置")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findMessageSettingByUserId(Long userId);

    @ApiOperation("查询某个用户收到的通知")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findMessageListByUserId(Long userId);

    @ApiOperation("分页查询系统通知")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findMessageListByPage(long currentPage, long pageSize, QueryPageRequestMessage queryPageRequestMessage);

    @ApiOperation("查询某个通知的详情")
    @ApiImplicitParam(name = "messageId", value = "通知id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findMessageById(long messageId);

    @ApiOperation("管理员发布系统通知")
    @ApiImplicitParam(name = "adminId", value = "发布通知的管理员id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse inputMessage(long adminId, Message message);

    @ApiOperation("管理员修改系统通知")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminId", value = "修改通知的管理员id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "messageId", value = "被修改的通知id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse editMessage(long adminId, long messageId, Message message);

    @ApiOperation("查询用户未读的系统通知或事件提醒数量")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findUnreadCountByUserId(long userId);

    @ApiOperation("查询用户收到的事件提醒")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findEventRemindListByUserId(long userId);

    @ApiOperation("向指定窗口推送消息")
    @ApiImplicitParam(name = "toId", value = "接收方id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse push(long toId, ChatMessage message);

    @ApiOperation("获取与指定用户的聊天消息内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromId", value = "发送方id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "toId", value = "接收方id", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findSelfMessageList(long fromId, long toId);

    @ApiOperation("获取某个用户的最近聊天用户列表")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findLatelyUserList(long userId);
}
