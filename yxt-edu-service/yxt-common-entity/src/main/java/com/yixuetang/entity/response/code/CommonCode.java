package com.yixuetang.entity.response.code;

import lombok.ToString;

/**
 * @author Colin
 * @version 1.0.0
 * @description 通用的响应代码枚举类
 * @date 2020/10/23 15:43
 */
@ToString
public enum CommonCode implements ResponseCode {

    SUCCESS( true, 20000, "操作成功！" ),

    FAIL( false, 20001, "操作失败！" ),

    INVALID_PARAM( false, 20002, "非法参数！" ),

    SERVER_ERROR( false, 29999, "抱歉，系统繁忙，请稍后重试！" );

    // 操作是否成功
    private boolean success;
    // 响应代码
    private int code;
    // 响应消息
    private String message;

    CommonCode(boolean success, int code, String message) {
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
