package com.yixuetang.entity.response.code.course;
import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块响应代码
 * @date 2020/10/29 12:24
 */
public enum CourseCode implements ResponseCode {

    COURSE_NOT_FOUND(false, 50000, "查询不到课程！"),

    DELETE_COURSE_FAIL(false, 50001, "删除课程失败！"),

    JOIN_COURSE_FAIL(false, 50002, "加课失败，请勿重复加课"),

    INSERT_COURSE_FAIL(false, 50003, "非教师操作！"),;

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
