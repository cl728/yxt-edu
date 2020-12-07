package com.yixuetang.exam.controller;

import com.yixuetang.api.exam.ExamControllerApi;
import com.yixuetang.exam.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
