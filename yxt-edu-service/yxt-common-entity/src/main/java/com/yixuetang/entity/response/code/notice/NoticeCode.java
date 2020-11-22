package com.yixuetang.entity.response.code.notice;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块中公告的响应代码
 * @date 2020/11/12 12:36
 */
public enum NoticeCode implements ResponseCode {

    NOTICE_NOT_FOUND(false, 80000, "找不到公告"),

    INSERT_NOTICE_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER(false, 80001, "新增公告失败，当前教师不是此课程原授课教师！"),

    INSERT_NOTICE_FAIL_TITLE_IS_NULL(false, 80002, "新增公告失败，公告标题不能为空！"),

    INSERT_NOTICE_FAIL_CONTENT_IS_NULL(false, 80003, "新增公告失败，公告内容不能为空！");

    private boolean success;
    private int code;
    private String message;

    NoticeCode(boolean success, int code, String message) {
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
