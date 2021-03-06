package com.yixuetang.entity.response.code.homework;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 作业模块响应代码
 * @date 2020/11/11 21:46
 */
public enum HomeworkCode implements ResponseCode {

    INSERT_HOMEWORK_FAIL_TITLE_IS_NULL(false, 70000, "新增作业失败，作业题目不能为空！"),

    INSERT_HOMEWORK_FAIL_DEADLINE_LESS_THAN_30_MINS(false, 70001, "新增作业失败，提交截止日期不能少于30分钟！"),

    INSERT_HOMEWORK_FAIL_TOTAL_SCORE_INVAILD(false, 70002, "新增作业失败，作业总分值设置应大于0小于等于100！"),

    INSERT_HOMEWORK_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER(false, 70003, "新增作业失败，当前教师不是该课程的教师！"),

    INSERT_HOMEWORK_FAIL_DEADLINE_IS_NULL(false, 70004, "新增作业失败，截止日期不能为空！"),

    HOMEWORK_NOT_EXIST(false, 70005, "作业不存在！"),

    HOMEWORK_IS_NOT_BELONG_TO_THIS_COURSE(false, 70006, "作业不属于该门课程！"),

    UPDATE_HOMEWORK_FAIL_TITLE_IS_NULL(false, 70007, "作业题目不能为空！"),

    UPDATE_HOMEWORK_FAIL_DESCRIPTION_IS_NULL(false, 70007, "作业题目不能为空！"),

    STUDENT_HOMEWORK_NOT_EXIST(false, 70008, "学生-作业记录不存在！"),

    THE_SCORE_CAN_NOT_BE_LOWER_THAN_ZERO(false, 70009, "评分不能低于0"),

    THE_SCORE_EXCEEDS_THE_MAXIMUM(false, 70010, "评分超出最大分值");

    private boolean success;
    private int code;
    private String message;

    HomeworkCode(boolean success, int code, String message) {
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
