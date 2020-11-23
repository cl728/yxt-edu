package com.yixuetang.entity.response.code.comment;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 评论模块响应代码
 * @date 2020/11/23 09:18
 */
public enum CommentCode implements ResponseCode {

    COMMENT_NOT_FOUND(false, 50000, "查询不到评论！");

    private boolean success;
    private int code;
    private String message;

    CommentCode(boolean success, int code, String message) {
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
