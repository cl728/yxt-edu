package com.yixuetang.exam.controller;

import com.yixuetang.api.exam.ExamControllerApi;
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.InsertQuestion;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.exam.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 考试模块的控制层
 * @date 2020/12/07 10:24
 */
@RestController
@RequestMapping("exam")
public class ExamController implements ExamControllerApi {

    @Autowired
    private ExamService examService;

    @Override
    @PutMapping("status/examId/{examId}/actionType/{actionType}")
    public CommonResponse updateStatus(@PathVariable long examId, @PathVariable int actionType) {
        return this.examService.updateStatus(examId, actionType);
    }

    @Override
    @DeleteMapping("examId/{examId}")
    public CommonResponse deleteById(@PathVariable long examId) {
        return this.examService.deleteById(examId);
    }

    @Override
    @PostMapping("courseId/{courseId}")
    public CommonResponse newExam(@PathVariable long courseId, @RequestBody InsertExam insertExam) {
        return this.examService.newExam(courseId, insertExam);
    }

    @Override
    @PutMapping("topNum/examId/{examId}")
    public CommonResponse examTop(@PathVariable long examId) {
        return this.examService.examTop(examId);
    }

    @Override
    @PostMapping("question/examId/{examId}/teacherId/{teacherId}")
    public CommonResponse saveQuestion(@PathVariable long examId, @PathVariable long teacherId
            , @RequestBody InsertQuestion insertQuestion) {
        return this.examService.saveQuestion(examId, teacherId, insertQuestion);
    }

    @Override
    @DeleteMapping("question/examId/{examId}/questionNumber/{questionNumber}")
    public CommonResponse deleteQuestion(@PathVariable long examId, @PathVariable long questionNumber) {
        return this.examService.deleteQuestion(examId, questionNumber);
    }

    @Override
    @GetMapping("courseId/{courseId}/userId/{userId}")
    public QueryResponse findListByCourseId(@PathVariable("courseId") long courseId,
                                            @PathVariable("userId") long userId) {
        return this.examService.findListByCourseId(courseId, userId);
    }
}

