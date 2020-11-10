package com.yixuetang.user.service;


import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.request.user.*;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块服务层接口
 * @date 2020/10/23 16:08
 */
public interface UserService {

    /**
     * 查询一个用户个人信息
     *
     * @param id 用户主键id
     * @return 响应结果实体类
     */
    QueryResponse findOneUser(long id);

    /**
     * 分页查询用户
     *
     * @param currentPage          当前页码数
     * @param pageSize             每页显示条数
     * @param queryPageRequestUser 分页查询条件实体类
     * @return 响应结果实体类
     */
    QueryResponse findByPage(long currentPage, long pageSize, QueryPageRequestUser queryPageRequestUser);

    /**
     * 分页查询角色
     *
     * @param currentPage 当前页码数
     * @param pageSize    每页显示条数
     * @return 响应结果实体类
     */
    QueryResponse findRolesByPage(long currentPage, long pageSize);

    /**
     * 查询所有学校
     *
     * @return 响应结果实体类
     */
    QueryResponse findAllSchools();

    /**
     * 发送验证码到用户邮箱并根据 type 以不同的 key 存储到 redis 上
     *
     * @param loginUser 登录用户实体类
     * @param sendType  发送验证码到何处 1手机 2邮箱
     * @param codeType  因何发送验证码 1登录 2注册 3修改密码 4换绑手机/邮箱
     * @return 响应结果实体类
     */
    CommonResponse sendCode(LoginUser loginUser, int sendType, int codeType);

    /**
     * 完成用户注册逻辑
     *
     * @param registerUser 注册用户实体类
     * @return 响应结果实体类
     */
    CommonResponse register(RegisterUser registerUser);

    /**
     * 完成用户信息修改
     *
     * @param id         用户主键id
     * @param updateUser 修改用户实体类
     * @return 响应结果实体类
     */
    CommonResponse updateUser(long id, UpdateUser updateUser);

    /**
     * 修改用户密码
     *
     * @param id           用户主键id
     * @param passwordUser 修改用户密码实体类
     * @return 响应结果实体类
     */
    CommonResponse updatePassword(long id, PasswordUser passwordUser);

    /* 分页查询学校
     *
     * @param currentPage 当前页码数
     * @param pageSize    每页显示条数
     * @return 响应结果实体类
     */
    QueryResponse findSchoolsByPage(long currentPage, long pageSize);

    /**
     * 换绑用户邮箱
     *
     * @param id        用户主键id
     * @param emailUser 换绑用户邮箱
     * @return 响应结果实体类
     */
    CommonResponse updateEmail(long id, EmailUser emailUser);

    /**
     * 换绑手机号码
     *
     * @param id        用户主键id
     * @param phoneUser 换绑手机号码
     * @return 响应结果实体类
     */
    CommonResponse updatePhone(long id, PhoneUser phoneUser);

    /**
     * 管理员删除用户
     *
     * @param userId 用户主键id
     * @return 响应结果实体类
     */
    CommonResponse delById(long userId);

    /**
     * 用户更换头像
     *
     * @param id   用户主键id
     * @param file 头像文件
     * @return 新上传的头像链接
     */
    String updateAvatar(long id, MultipartFile file);

    /**
     * 分页查询某门课程下的成员
     *
     * @param courseId    课程id
     * @param currentPage 当前页码数
     * @param pageSize    每页显示条数
     * @return 响应结果实体类
     */
    QueryResponse findPageByCourseId(long courseId, long currentPage, long pageSize);
}
