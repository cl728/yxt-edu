package com.yixuetang.course.controller;

import com.yixuetang.api.course.CourseControllerApi;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.request.course.TransferCourse;
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
    @PutMapping("teacherId/id/{courseId}/{teacherId}")
    public CommonResponse transferCourses(@PathVariable Long courseId, @PathVariable Long teacherId, @RequestBody TransferCourse transferCourse) {
        return this.courseService.transferCourses(courseId, teacherId, transferCourse);
    }

    @Override
    @GetMapping
    public QueryResponse findAllCourses() {
        return this.courseService.findAllCourses();
    }

    @Override
    @DeleteMapping("id/{userId}/{courseId}")
    public CommonResponse deleteCourse(@PathVariable Long userId, @PathVariable Long courseId) {
        return this.courseService.deleteCourse(userId, courseId);
    }

    @Override
    @PostMapping("id/{userId}/{code}")
    public CommonResponse joinCourse(@PathVariable Long userId, @PathVariable String code) {
        return this.courseService.joinCourse(userId, code);
    }

    @Override
    @PostMapping("id/{teacherId}")
    public CommonResponse saveCourse(@PathVariable Long teacherId, @RequestBody InsertCourse course) {
        return this.courseService.saveCourse(teacherId, course);
    }

    @Override
    @GetMapping("page/{currentPage}/{pageSize}")
    public QueryResponse findByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.courseService.findByPage(currentPage, pageSize);
    }

    @Override
    @GetMapping("listAllUserCourse/{userId}")
    public QueryResponse findByUserId(@PathVariable long userId) {
        return this.courseService.findByUserId(userId);
    }

    @Override
    @GetMapping("listAllTeacherCourse/{teacherId}")
    public QueryResponse findByTeacherId(@PathVariable long teacherId) {
        return this.courseService.findByTeacherId(teacherId);
    }

    @Override
    @GetMapping("updateTopSCourse/{courseId}/{userId}/{isTop}")
    public CommonResponse updateTopSCourse(@PathVariable long courseId, @PathVariable long userId, @PathVariable boolean isTop) {
        return courseService.updateTopSCourse(courseId, userId, isTop);
    }

    @Override
    @GetMapping("updateTopTCourse/{courseId}/{teacherId}/{isTop}")
    public CommonResponse updateTopTCourse(@PathVariable long courseId, @PathVariable long teacherId, @PathVariable boolean isTop) {
        return courseService.updateTopTCourse(courseId, teacherId, isTop);
    }

}
