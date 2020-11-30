package com.yixuetang.entity.response.code.remind;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 实践提醒的响应代码
 * @date 2020/11/30 12:12
 */
public enum RemindCode implements ResponseCode {

    REMIND_NOT_FOUND(false, 110000, "找不到该事件提醒！"),

    REMIND_NOT_BELONGS_TO_THIS_USER(false, 110001, "该用户不是此事件提醒的接收者！");

    private boolean success;
    private int code;
    private String message;

    RemindCode(boolean success, int code, String message) {
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
