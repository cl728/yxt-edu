package com.yixuetang.user.service;


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
     * 查询所有用户
     *
     * @return 包含用户列表数据，用户列表总数以及响应操作码的结果
     */
    QueryResponse findAll();

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
     * @param updateUser 修改用户实体类
     * @return 响应结果实体类
     */
    CommonResponse updateUser(UpdateUser updateUser);
}
