package com.yixuetang.entity.response.code.course;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块响应代码
 * @date 2020/10/29 12:24
 */
public enum CourseCode implements ResponseCode {

    COURSE_NOT_FOUND(false, 30000, "查询不到课程！");

    private boolean success;
    private int code;
    private String message;

    CourseCode(boolean success, int code, String message) {
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
