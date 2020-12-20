package com.yixuetang.exam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yixuetang.api.exam.ExamControllerApi;
import com.yixuetang.entity.request.exam.EditStudentScoreRequest;
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.question.ExamQuestionRequest;
import com.yixuetang.entity.request.exam.question.ExamQuestionStudentRequest;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.exam.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger( ExamController.class );

    @Override
    @PutMapping("status/examId/{examId}/teacherId/{teacherId}/actionType/{actionType}")
    public CommonResponse updateStatus(@PathVariable("examId") long examId,
                                       @PathVariable("teacherId") long teacherId,
                                       @PathVariable int actionType) {
        return this.examService.updateStatus( examId, teacherId, actionType );
    }

    @Override
    @GetMapping("info/examId/{examId}")
    public QueryResponse findExamById(@PathVariable long examId) {
        return this.examService.findExamById( examId );
    }

    @Override
    @DeleteMapping("examId/{examId}")
    public CommonResponse deleteById(@PathVariable long examId) {
        return this.examService.deleteById( examId );
    }

    @Override
    @PostMapping("courseId/{courseId}")
    public CommonResponse newExam(@PathVariable long courseId, @RequestBody InsertExam insertExam) {
        return this.examService.newExam( courseId, insertExam );
    }

    @Override
    @PutMapping("topNum/examId/{examId}")
    public CommonResponse examTop(@PathVariable long examId) {
        return this.examService.examTop( examId );
    }

    @Override
    @PostMapping("question/examId/{examId}/teacherId/{teacherId}")
    public CommonResponse saveQuestion(@PathVariable long examId,
                                       @PathVariable long teacherId,
                                       @RequestBody ExamQuestionRequest examQuestionRequest) {
        try {
            return this.examService.saveQuestion( examId, teacherId, examQuestionRequest );
        } catch (JsonProcessingException e) {
            LOGGER.error( "JSON解析发生异常！异常原因：{}", e );
            return new CommonResponse( CommonCode.FAIL );
        }
    }

    @Override
    @DeleteMapping("question/examId/{examId}/questionNumber/{questionNumber}")
    public CommonResponse deleteQuestion(@PathVariable long examId, @PathVariable long questionNumber) {
        return this.examService.deleteQuestion( examId, questionNumber );
    }

    @Override
    @GetMapping("courseId/{courseId}/userId/{userId}")
    public QueryResponse findListByCourseId(@PathVariable("courseId") long courseId,
                                            @PathVariable("userId") long userId) {
        return this.examService.findListByCourseId( courseId, userId );
    }

    @Override
    @PutMapping("info/examId/{examId}")
    public CommonResponse updateExam(@PathVariable long examId, @RequestBody InsertExam insertExam) {
        return this.examService.updateExam( examId, insertExam );
    }

    @Override
    @GetMapping("question/examId/{examId}/userId/{userId}")
    public QueryResponse findListByExamId(@PathVariable("examId") long examId,
                                          @PathVariable("userId") long userId) {
        return this.examService.findListByExamId( examId, userId );
    }

    @Override
    @GetMapping("question/examQuestionStudent/examId/{examId}/questionNumber/{questionNumber}/studentId/{studentId}")
    public QueryResponse getExamQuestionStudent(@PathVariable("examId") long examId,
                                                @PathVariable("questionNumber") int questionNumber,
                                                @PathVariable("studentId") long studentId) {
        return this.examService.getExamQuestionStudent( examId, questionNumber, studentId );
    }

    @Override
    @PostMapping("question/examQuestionStudent/examId/{examId}/questionNumber/{questionNumber}/studentId/{studentId}")
    public CommonResponse saveExamQuestionStudent(@PathVariable("examId") long examId,
                                                  @PathVariable("questionNumber") int questionNumber,
                                                  @PathVariable("studentId") long studentId,
                                                  @RequestBody ExamQuestionStudentRequest examQuestionStudentRequest) {
        return this.examService.saveExamQuestionStudent( examId, questionNumber, studentId, examQuestionStudentRequest );
    }

    @Override
    @PostMapping("examId/{examId}/studentId/{studentId}")
    public CommonResponse submitExam(@PathVariable("examId") long examId,
                                     @PathVariable("studentId") long studentId) {
        return this.examService.submitExam( examId, studentId );
    }

    @Override
    @GetMapping("examStudent/examId/{examId}/studentId/{studentId}")
    public QueryResponse getExamStudent(@PathVariable("examId") long examId,
                                        @PathVariable("studentId") long studentId) {
        return this.examService.getExamStudent( examId, studentId );
    }

    @Override
    @PutMapping("examQuestionStudent/score")
    public CommonResponse editStudentScore(@RequestBody EditStudentScoreRequest editStudentScoreRequest) {
        return this.examService.editStudentScore( editStudentScoreRequest );
    }

    @Override
    @GetMapping("examStudentScores/courseId/{courseId}/teacherId/{teacherId}")
    public QueryResponse getExamStudentScores(@PathVariable long courseId, @PathVariable long teacherId) {
        return this.examService.getExamStudentScores( courseId, teacherId );
    }
}

