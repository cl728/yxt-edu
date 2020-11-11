package com.yixuetang.homework.controller;

import com.yixuetang.api.homework.HomeworkControllerApi;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.homework.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程模块下的作业控制层
 * @date 2020/11/11 16:51
 */
@RestController
@RequestMapping("homework")
public class HomeworkController implements HomeworkControllerApi {

    @Autowired
    private HomeworkService homeworkService;

    @Override
    @GetMapping("courseId/{courseId}/{userId}")
    public QueryResponse findByCourseId(@PathVariable long courseId, @PathVariable long userId) {
        return this.homeworkService.findByCourseId( courseId, userId );
    }

}
