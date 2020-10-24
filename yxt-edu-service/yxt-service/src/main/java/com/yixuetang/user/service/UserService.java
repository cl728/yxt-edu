package com.yixuetang.user.service;


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

}
