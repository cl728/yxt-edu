package com.yixuetang.entity.response.code.message;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 消息模块中公告的响应代码
 * @date 2020/11/25 17:02
 */
public enum MessageCode implements ResponseCode {

    MESSAGE_NOT_FOUND( false, 100000, "找不到消息" ),

    PUSH_FAIL_TO_USER_NOT_FOUND( false, 100001, "找不到指定推送用户！" );

    private boolean success;
    private int code;
    private String message;

    MessageCode(boolean success, int code, String message) {
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
