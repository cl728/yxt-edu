package com.yixuetang.course.controller;

import com.yixuetang.api.course.CourseControllerApi;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.request.course.InsertCourse;
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
    @DeleteMapping("id/{id}")
    public CommonResponse deleteCourse(@PathVariable Long id) {
        return this.courseService.deleteCourse( id );
    }

    @Override
    @PostMapping("id/{id}/{code}")
    public CommonResponse joinCourse(@PathVariable Long id, @PathVariable String code) {
        return this.courseService.joinCourse( id, code );
    }

    @Override
    @PostMapping("id/{teacherId}")
    public CommonResponse saveCourse(@PathVariable Long teacherId, @RequestBody InsertCourse course) {
        return this.courseService.saveCourse( teacherId, course );
    }

    @Override
    @GetMapping("page/{currentPage}/{pageSize}")
    public QueryResponse findByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.courseService.findByPage( currentPage, pageSize );
    }

}
