package com.yixuetang.exam.service;

import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.response.CommonResponse;

/**
 * @author Curtis
 * @version 1.0.0
 * @date 2020/12/07 10:25
 */
public interface ExamService {
    /**
     * 教师在某一课程下新建测试
     *
     * @param courseId
     * @return  响应结果实体类
     */
    CommonResponse newExam(long courseId, InsertExam insertExam);
}
