package com.yixuetang.entity.response.code.auth;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Colin
 * @version 1.0.0
 * @description 授权模块响应代码
 * @date 2020/10/23 21:19
 */
public enum AuthCode implements ResponseCode {

    LOGIN_FAIL_BY_PWD( false, 40000, "用户名或密码错误！" ),

    LOGIN_FAIL_BY_EMAIL( false, 40001, "验证码错误！" ),

    LOGIN_SUCCESS( true, 40002, "登录成功！" ),

    VERIFY_SUCCESS( true, 40003, "校验用户身份成功！" );

    private boolean success;
    private int code;
    private String message;

    AuthCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return this.success;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

}
