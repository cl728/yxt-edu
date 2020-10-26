package com.yixuetang.user.controller;

import com.yixuetang.api.user.UserControllerApi;
import com.yixuetang.entity.request.user.EmailUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.service.UserService;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    @GetMapping("schools")
    public QueryResponse findAllSchools() {
        return this.userService.findAllSchools();
    }

    @Override
    @PostMapping("code/{codeType}")
    public CommonResponse sendCode(@PathVariable int codeType, @RequestBody EmailUser emailUser) {
        return this.userService.sendCode( emailUser.getEmail(), codeType );
    }
}
