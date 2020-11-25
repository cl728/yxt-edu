package com.yixuetang.entity.response.code.resource;

import com.yixuetang.entity.response.code.ResponseCode;

public enum ResourceCode implements ResponseCode {

    CONTENT_TYPE_INVALID( false, 60000, "暂不支持该文件类型！" ),

    PARENT_RESOURCE_NOT_EXISTS( false, 60001, "父级资源不存在！" ),

    HOMEWORK_RESOURCE_NOT_EXISTS( false, 60002, "学生作业资源不存在！" ),

    RESOURCE_NOT_EXISTS( false, 60003, "资源不存在！" ),

    RESOURCE_NAME_CAN_NOT_BE_EMPTY( false, 60004, "资源名称不能为空！" );

    private boolean success;
    private int code;
    private String message;

    ResourceCode(boolean success, int code, String message) {
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
