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

    @ApiOperation("根据用户名和密码进行用户认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginType", value = "登录方式", required = true,
                    paramType = "path", dataType = "int")
    })
    CommonResponse login(int loginType, LoginUser loginUser,
                        HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("根据用户携带的 token 信息验证用户身份")
    @ApiImplicitParam(name = "token", value = "用户携带的 token 信息", required = true,
            paramType = "path", dataType = "String")
    CommonResponse verify(String token, HttpServletRequest request, HttpServletResponse response);
}
