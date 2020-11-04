package com.yixuetang.course.controller;

import com.yixuetang.api.course.CourseControllerApi;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.request.course.QueryPageRequestCourse;
import com.yixuetang.entity.request.course.TransferCourse;
import com.yixuetang.entity.request.course.UpdateCourse;
import com.yixuetang.entity.request.user.DelCourseUser;
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
    @PutMapping("info/id/{courseId}/{teacherId}")
    public CommonResponse updateCourses(@PathVariable Long courseId, @PathVariable Long teacherId, @RequestBody UpdateCourse updateCourse) {
        return this.courseService.updateCourses(courseId, teacherId, updateCourse);
    }

    @Override
    @GetMapping("info/id/{courseId}")
    public QueryResponse findById(@PathVariable long courseId) {
        return this.courseService.findById( courseId );
    }

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
    @DeleteMapping("id/{courseId}")
    public CommonResponse deleteCourse(@PathVariable Long courseId, @RequestBody DelCourseUser delCourseUser) {
        return this.courseService.deleteCourse(courseId, delCourseUser);
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
    public QueryResponse findByPage(@PathVariable long currentPage, @PathVariable long pageSize, QueryPageRequestCourse queryPageRequestCourse) {
        return this.courseService.findByPage(currentPage, pageSize, queryPageRequestCourse);
    }

    @Override
    @GetMapping("userId/{userId}")
    public QueryResponse findCoursesByUserId(@PathVariable long userId) {
        return this.courseService.findCoursesByUserId(userId);
    }


    @Override
    @PutMapping("topNum/id/{courseId}/{userId}")
    public CommonResponse updateTopCourse(@PathVariable long courseId, @PathVariable long userId) {
        return courseService.updateTopCourse(courseId, userId);
    }

    @Override
    @PutMapping("isFiled/id/{courseId}/{userId}")
    public CommonResponse file(@PathVariable long courseId, @PathVariable long userId) {
        return this.courseService.file(courseId, userId);
    }


}
