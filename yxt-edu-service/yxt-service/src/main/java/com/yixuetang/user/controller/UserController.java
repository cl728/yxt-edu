package com.yixuetang.user.controller;

import com.yixuetang.api.user.UserControllerApi;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块控制层
 * @date 2020/10/23 16:07
 */
@RestController
@RequestMapping("users")
public class UserController implements UserControllerApi {

    @Autowired
    private UserService userService;

    @Override
    @GetMapping
    public QueryResponse findAll() {
        return this.userService.findAll();
    }

    @Override
    @GetMapping("roles")
    public QueryResponse findAllRoles() {
        return this.userService.findAllRoles();
    }
}
