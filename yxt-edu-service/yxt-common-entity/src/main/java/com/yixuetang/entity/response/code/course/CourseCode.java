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

    SET_TOP_FAIL(false, 50002, "课程置顶失败，课程已经置顶"),

    INSERT_COURSE_FAIL(false, 50003, "非教师操作！"),

    TRANSFER_COURSE_FAIL(false, 50004, "教师转让课程失败！"),

    TRANSFER_COURSE_FAIL_COURSE_NOT_FOUND(false, 50005, "教师转让课程失败，未找到该课程！"),

    TRANSFER_COURSE_FAIL_PASSWORD_WRONG(false, 50006, "教师转让课程失败，请求转入教师的密码错误！"),

    TRANSFER_COURSE_FAIL_EMAIL_NOT_EFFECTIVE(false, 50007, "教师转让课程失败，接受转入课程的教师邮箱无效！"),

    TRANSFER_COURSE_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER(false, 50008, "教师转让课程失败，该教师不是此课程原授课教师！"),

    TRANSFER_COURSE_FAIL_SAME_TEACHER(false, 50009, "教师转让课程失败，原授课教师不能转让给自己！");

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
