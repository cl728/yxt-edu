package com.yixuetang.exam.service;

import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.InsertQuestion;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

/**
 * @author Curtis
 * @version 1.0.0
 * @date 2020/12/07 10:25
 */
public interface ExamService {


    /**
     * 教师删除某一考试（测试）
     *
     * @param examId
     * @return
     */
    CommonResponse deleteById(long examId);

    /**
     * 教师在某一课程下新建测试
     *
     * @param courseId 课程id
     * @return 响应结果实体类
     */
    CommonResponse newExam(long courseId, InsertExam insertExam);

    /**
     * 查询某一测试（考试）的基本信息
     *
     * @param examId    测试（考试）id
     * @return
     */
    QueryResponse findExamById(long examId);

    /**
     * 教师置顶/取消置顶测试（考试）
     *
     * @param examId 测试（考试）id
     * @return 响应结果实体类
     */
    CommonResponse examTop(long examId);

    /**
     * 教师保存题目
     *
     * @param examId         测试（考试）id
     * @param teacherId      教师id
     * @param insertQuestion 新增的题目实体
     * @return 响应结果实体类
     */
    CommonResponse saveQuestion(long examId, long teacherId, InsertQuestion insertQuestion);

    /**
     * 教师删除试卷的某道题目
     *
     * @param examId         测试（考试）id
     * @param questionNumber 题目编号
     * @return 响应结果实体类
     */
    CommonResponse deleteQuestion(long examId, long questionNumber);

    /**
     * 用户查询某门课程下的测试（考试）列表
     *
     * @param courseId 课程id
     * @param userId   用户id
     * @return 响应结果实体类
     */
    QueryResponse findListByCourseId(long courseId, long userId);

    /**
     * 教师发布/重新发布/取消发布测试（考试）
     *
     * @param examId     课程id
     * @param actionType 操作类型，0⾸次发布 1重新发布 2取消发布，路径变量
     * @return 响应结果实体类
     */
    CommonResponse updateStatus(long examId, int actionType);
}
