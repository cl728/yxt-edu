package com.yixuetang.api.auth;

import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description 认证模块的api
 * @date 2020/10/23 21:00
 */
@Api(value = "认证模块接口", description = "认证模块接口，提供认证服务接口")
public interface AuthControllerApi {

    @ApiOperation("根据不同登录方式进行用户授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userType", value = "用户类型 1管理员 2普通用户", required = true,
                    paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "loginType", value = "登录方式 1账号登录 2邮箱登录", required = true,
                    paramType = "path", dataType = "int")
    })
    CommonResponse login(int userType, int loginType, LoginUser loginUser,
                         HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("根据不同用户携带的 token 信息验证用户身份")
    @ApiImplicitParam(name = "userType", value = "用户类型 1管理员 2普通用户", required = true,
            paramType = "path", dataType = "int")
    CommonResponse verify(int userType, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("用户退出登录")
    @ApiImplicitParam(name = "userType", value = "用户类型 1管理员 2普通用户", required = true,
            paramType = "path", dataType = "int")
    CommonResponse logout(int userType, HttpServletRequest request, HttpServletResponse response);
}
