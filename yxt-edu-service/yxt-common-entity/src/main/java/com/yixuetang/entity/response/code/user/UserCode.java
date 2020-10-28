package com.yixuetang.entity.response.code.user;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块响应代码
 * @date 2020/10/23 16:42
 */
public enum UserCode implements ResponseCode {

    USER_NOT_FOUND( false, 30000, "查询不到用户！" ),

    EMAIL_NOT_REGISTERED( false, 30001, "该邮箱尚未注册，请先进行注册！" ),

    EMAIL_HAS_BEEN_REGISTERED( false, 30002, "该邮箱地址已被注册！" ),

    REGISTER_FAIL_CODE_WRONG( false, 30003, "验证码错误！" ),

    REGISTER_FAIL_USERNAME_CONFLICT( false, 30004, "此用户名太受欢迎，请更换一个吧！" ),

    REGISTER_FAIL_ROLE_NAME_NOT_FOUND( false, 30005, "角色名称不存在！" ),

    UPDATE_FAIL_ROLE_NAME_NOT_FOUND( false, 30006, "角色名称不存在！" ),

    UPDATE_FAIL_TSNO_CONFLICT( false, 30007, "学/工号已存在！！" ),

    UPDATE_PASSWORD_FAIL_OLD_PASSWORD_WRONG( false, 30008, "旧密码错误！" ),

    UPDATEPASSWORD_FAIL_CODE_WRONG( false, 30009, "验证码错误！" ),

    UPDATE_EMAIL_FAIL_EMAIL_ALREADY_EXISTS( false, 30010, "换绑邮箱失败，该邮箱已存在！" ),

    UPDATE_EMAIL_FAIL_CODE_WRONG( false, 30009, "验证码错误！" );

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
