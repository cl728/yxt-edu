package com.yixuetang.api.user;

import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.request.user.*;
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
    QueryResponse findByPage(long currentPage, long pageSize, QueryPageRequestUser queryPageRequestUser);

    @ApiOperation("换绑邮箱")
    @ApiImplicitParam(name = "id", value = "用户主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updateEmail(long id, EmailUser emailUser, HttpServletRequest request);

    @ApiOperation("换绑手机号码")
    @ApiImplicitParam(name = "id", value = "用户主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updatePhone(long id, PhoneUser phoneUser, HttpServletRequest request);

    @ApiOperation("更换头像")
    @ApiImplicitParam(name = "id", value = "用户主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updateAvatar(long id, AvatarUser avatarUser, HttpServletRequest request, HttpServletResponse response);

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

    @ApiOperation("管理员删除用户")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "path", dataType = "long")
    CommonResponse delById(long userId);

    @ApiOperation("分页条件查询某门课程下的成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "search", value = "搜索字段（学号、姓名）",
                    paramType = "query", dataType = "String")
    })
    QueryResponse findPageByCourseId(long courseId, long currentPage, long pageSize, String search);

    @ApiOperation("分页条件查询某次作业下的成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "search", value = "搜索字段（学号、姓名）",
                    paramType = "query", dataType = "String")
    })
    QueryResponse findPageByHomeworkId(long homeworkId, long currentPage, long pageSize, String search);

    @ApiOperation("查询某个用户加入（或拥有）的课程中的用户成员列表")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findCourseUsers(long userId);

    @ApiOperation("分页条件查询某次测试（考试）下的成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "测试（考试）id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "search", value = "搜索字段（学号、姓名）",
                    paramType = "query", dataType = "String")
    })
    QueryResponse findPageByExamId(long examId, long currentPage, long pageSize, String search);

    @ApiOperation("查询用户类别及其数量")
    QueryResponse findUserTypeCount();

    @ApiOperation("管理员新增用户")
    CommonResponse addUser(UserForm userForm);
}
