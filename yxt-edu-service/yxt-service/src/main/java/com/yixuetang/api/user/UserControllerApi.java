package com.yixuetang.api.user;

import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.request.user.EmailUser;
import com.yixuetang.entity.request.user.PasswordUser;
import com.yixuetang.entity.request.user.RegisterUser;
import com.yixuetang.entity.request.user.UpdateUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块的api
 * @date 2020/10/23 16:05
 */
@Api(value = "用户模块管理接口", description = "用户模块管理接口，提供用户的增、删、改、查")
public interface UserControllerApi {

    @ApiOperation("查询个人信息")
    @ApiImplicitParam(name = "id", value = "用户主键id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findOneUser(long id, HttpServletRequest request);

    @ApiOperation("修改个人信息")
    @ApiImplicitParam(name = "id", value = "用户主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updateUser(long id, UpdateUser updateUser, HttpServletRequest request);

    @ApiOperation("修改密码")
    @ApiImplicitParam(name = "id", value = "用户主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updatePassword(long id, PasswordUser passwordUser, HttpServletRequest request);

    @ApiOperation("分页查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findByPage(long currentPage, long pageSize);

    @ApiOperation("换绑邮箱")
    @ApiImplicitParam(name = "id", value = "用户主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updateEmail(long id, EmailUser emailUser, HttpServletRequest request);

    @ApiOperation("分页查询角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findRolesByPage(long currentPage, long pageSize);

    @ApiOperation("查询所有学校")
    QueryResponse findAllSchools();

    @ApiOperation("分页查询学校")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findSchoolsByPage(long currentPage, long pageSize);

    @ApiOperation("发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sendType", value = "发送验证码到何处 1手机 2邮箱", required = true,
                    paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "codeType", value = "因何发送验证码 1登录 2注册 3修改密码 4换绑手机/邮箱", required = true,
                    paramType = "path", dataType = "int")
    })
    CommonResponse sendCode(int sendType, int codeType, LoginUser loginUser);

    @ApiOperation("用户注册")
    CommonResponse register(RegisterUser registerUser);

}
