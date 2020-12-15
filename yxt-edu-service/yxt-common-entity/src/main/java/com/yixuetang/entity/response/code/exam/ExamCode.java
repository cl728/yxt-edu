package com.yixuetang.entity.response.code.exam;

import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Hemon
 * @version 1.0
 * @date 2020/12/7 17:03
 * @description
 */
public enum ExamCode implements ResponseCode {

    INSERT_EXAM_FAIL_TITLE_IS_NULL( false, 80000, "测试题目不能为空！" ),

    INSERT_EXAM_FAIL_INTRODUCTION_IS_NULL( false, 80001, "测试简介不能为空！" ),

    UPDATE_EXAM_STATUS_FAIL_NO_QUESTION( false, 80002, "试卷⾥⾯没有试题，发布失败" ),

    CORRECTION_COMPLETED( true, 80003, "该试卷已经批改完毕" );

    private boolean success;
    private int code;
    private String message;

    ExamCode(boolean success, int code, String message) {
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
