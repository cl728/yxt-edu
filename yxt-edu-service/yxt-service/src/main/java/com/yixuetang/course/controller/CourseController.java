package com.yixuetang.course.controller;

import com.yixuetang.api.course.CourseControllerApi;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块控制层
 * @date 2020/10/29 11:16
 */
@RestController
@RequestMapping("courses")
public class CourseController implements CourseControllerApi {

    @Autowired
    private CourseService courseService;

    @Override
    @GetMapping
    public QueryResponse findAllCourses() {
        return this.courseService.findAllCourses();
    }

    @Override
    @DeleteMapping("/{id}")
    public CommonResponse deleteCourse(@PathVariable Long id) {
        return this.courseService.deleteCourse(id);
    }

}
