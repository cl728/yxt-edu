package com.yixuetang.exam.controller;

import com.yixuetang.api.exam.ExamControllerApi;
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.response.CommonResponse;
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
    @PostMapping("courseId/{courseId}")
    public CommonResponse newExam(@PathVariable long courseId, @RequestBody InsertExam insertExam) {
        return this.examService.newExam(courseId, insertExam);
    }
}

