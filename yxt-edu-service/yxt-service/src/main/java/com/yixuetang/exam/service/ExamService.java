package com.yixuetang.exam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.question.ExamQuestionRequest;
import com.yixuetang.entity.request.exam.question.ExamQuestionStudentRequest;
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
     * @param examId 测试（考试）id
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
     * @param examId              测试（考试）id
     * @param teacherId           教师id
     * @param examQuestionRequest 新增的题目实体
     * @return 响应结果实体类
     */
    CommonResponse saveQuestion(long examId, long teacherId, ExamQuestionRequest examQuestionRequest) throws JsonProcessingException;

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
     * @param teacherId  教师id
     * @param actionType 操作类型，0⾸次发布 1重新发布 2取消发布，路径变量
     * @return 响应结果实体类
     */
    CommonResponse updateStatus(long examId, long teacherId, int actionType);

    /**
     * 修改某个测试（考试）的基本信息
     *
     * @param examId     测试（考试）id
     * @param insertExam 编辑测试实体类
     * @return 响应结果实体类
     */
    CommonResponse updateExam(long examId, InsertExam insertExam);

    /**
     * 用户查询某次测试（考试）下的试题列表
     *
     * @param examId 测试（考试）id
     * @param userId 用户id
     * @return 响应结果实体类
     */
    QueryResponse findListByExamId(long examId, long userId);

    /**
     * 查询某个用户某次测试（考试）下的某道题的完成情况
     *
     * @param examId         测试（考试）id
     * @param questionNumber 题号
     * @param studentId      学生id
     * @return 响应结果实体类
     */
    QueryResponse getExamQuestionStudent(long examId, int questionNumber, long studentId);

    /**
     * 保存某个用户某次测试（考试）下的某道题的完成情况
     *
     * @param examId                     测试（考试）id
     * @param questionNumber             题号
     * @param studentId                  学生id
     * @param examQuestionStudentRequest 学生答题请求实体
     * @return 响应结果实体类
     */
    CommonResponse saveExamQuestionStudent(long examId, int questionNumber, long studentId, ExamQuestionStudentRequest examQuestionStudentRequest);

    /**
     * 学生提交测试（考试）
     *
     * @param examId    测试（考试）id
     * @param studentId 学生id
     * @return 响应结果实体类
     */
    CommonResponse submitExam(long examId, long studentId);

    /**
     * 获取学生某次考试情况
     *
     * @param examId    测试（考试）id
     * @param studentId 学生id
     * @return 响应结果实体类
     */
    QueryResponse getExamStudent(long examId, long studentId);

}
