package com.yixuetang.user.service.impl;

import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.Role;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.RoleMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块服务层接口实现类
 * @date 2020/10/23 16:09
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public QueryResponse findAll() {
        List<User> users = this.userMapper.findAll();
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( users, users.size() ) );
    }

    @Override
    public QueryResponse findAllRoles() {
        List<Role> roles = this.roleMapper.selectList( null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( roles, roles.size() ) );
    }
}
