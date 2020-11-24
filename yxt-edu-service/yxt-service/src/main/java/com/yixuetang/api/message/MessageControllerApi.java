package com.yixuetang.api.message;

import com.yixuetang.entity.message.UserMessageSetting;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/23 10:34
 */
@Api(value = "站内消息模块接口", description = "站内消息模块接口，提供站内消息服务接口")
public interface MessageControllerApi {

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

}
