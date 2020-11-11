package com.yixuetang.entity.response.code.file;

import com.yixuetang.entity.response.code.ResponseCode;

public enum FileCode implements ResponseCode {

    CONTENT_TYPE_INVALID(false, 60000, "暂不支持该文件类型！");

    private boolean success;
    private int code;
    private String message;

    FileCode(boolean success, int code, String message) {
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
