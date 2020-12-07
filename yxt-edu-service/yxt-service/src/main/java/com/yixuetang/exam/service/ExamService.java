package com.yixuetang.exam.service;

import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.InsertQuestion;
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

    /**
     * 教师置顶/取消置顶测试（考试）
     * @param examId  测试（考试）id
     * @return  响应结果实体类
     */
    CommonResponse examTop(long examId);

    /**
     * 教师保存题目
     * @param examId   测试（考试）id
     * @param teacherId   教师id
     * @param insertQuestion  新增的题目实体
     * @return  响应结果实体类
     */
    CommonResponse saveQuestion(long examId, long teacherId, InsertQuestion insertQuestion);
}
