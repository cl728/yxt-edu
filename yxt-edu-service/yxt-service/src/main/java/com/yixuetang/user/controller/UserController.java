package com.yixuetang.user.controller;

import com.yixuetang.api.user.UserControllerApi;
import com.yixuetang.entity.request.user.EmailUser;
import com.yixuetang.entity.request.user.RegisterUser;
import com.yixuetang.entity.request.user.UpdateUser;
import com.yixuetang.entity.request.user.PasswordUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("info/id/{id}")
    public CommonResponse updateUser(@PathVariable long id, @RequestBody UpdateUser updateUser) {
        return this.userService.updateUser( id, updateUser );
    }

    @Override
    @PutMapping("password/id/{id}")
    public CommonResponse updatePassword(@PathVariable long id, @RequestBody PasswordUser passwordUser) {
        return this.userService.updatePassword( id, passwordUser );
    }

    @Override
    @PutMapping("email/id/{id}")
    public CommonResponse updateEmail(@PathVariable long id, @RequestBody EmailUser emailUser) {
        return this.userService.updateEmail( id, emailUser );
    }

    @Override
    @GetMapping("page/{currentPage}/{pageSize}")
    public QueryResponse findByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.userService.findByPage( currentPage, pageSize );
    }

    @Override
    @GetMapping("roles/page/{currentPage}/{pageSize}")
    public QueryResponse findRolesByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.userService.findRolesByPage( currentPage, pageSize );
    }

    @Override
    @GetMapping("schools")
    public QueryResponse findAllSchools() {
        return this.userService.findAllSchools();
    }

    @Override
    @GetMapping("schools/page/{currentPage}/{pageSize}")
    public QueryResponse findSchoolsByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.userService.findSchoolsByPage( currentPage, pageSize );
    }

    @Override
    @PostMapping("code/{codeType}")
    public CommonResponse sendCode(@PathVariable int codeType, @RequestBody EmailUser emailUser) {
        return this.userService.sendCode( emailUser.getEmail(), codeType );
    }

    @Override
    @PostMapping
    public CommonResponse register(@RequestBody RegisterUser registerUser) {
        return this.userService.register( registerUser );
    }
}
