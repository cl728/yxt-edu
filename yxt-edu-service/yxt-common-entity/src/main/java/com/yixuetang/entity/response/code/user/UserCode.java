package com.yixuetang.entity.response.code.user;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块响应代码
 * @date 2020/10/23 16:42
 */
public enum UserCode implements ResponseCode {

    USER_NOT_FOUND( false, 30000, "查询不到用户！" );

    private boolean success;
    private int code;
    private String message;

    UserCode(boolean success, int code, String message) {
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
    }}
