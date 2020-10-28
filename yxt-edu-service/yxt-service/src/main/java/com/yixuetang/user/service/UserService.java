package com.yixuetang.user.service;


import com.yixuetang.entity.request.user.PasswordUser;
import com.yixuetang.entity.request.user.RegisterUser;
import com.yixuetang.entity.request.user.UpdateUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块服务层接口
 * @date 2020/10/23 16:08
 */
public interface UserService {

    /**
     * 分页查询用户
     *
     * @param currentPage 当前页码数
     * @param pageSize    每页显示条数
     * @return 响应结果实体类
     */
    QueryResponse findByPage(long currentPage, long pageSize);

    /**
     * 查询所有角色
     *
     * @return 响应结果实体类
     */
    QueryResponse findAllRoles();

    /**
     * 查询所有学校
     *
     * @return 响应结果实体类
     */
    QueryResponse findAllSchools();

    /**
     * 发送验证码到用户邮箱并根据 type 以不同的 key 存储到 redis 上
     *
     * @param email    用户邮箱地址
     * @param codeType 因何发送验证码 1登录 2注册 3修改密码
     * @return 响应结果实体类
     */
    CommonResponse sendCode(String email, int codeType);

    /**
     * 完成用户注册逻辑
     *
     * @param registerUser 注册用户实体类
     * @return 响应结果实体类
     */
    CommonResponse register(RegisterUser registerUser);

    /**
     * 完成用户信息修改
     * @param id 用户主键id
     * @param updateUser 修改用户实体类
     * @return 响应结果实体类
     */
    CommonResponse updateUser(long id, UpdateUser updateUser);

    /**
     * 完成用户密码修改
     * @param id 用户主键id
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
}
