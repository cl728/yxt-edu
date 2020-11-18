package com.yixuetang.homework.controller;

import com.yixuetang.api.homework.HomeworkControllerApi;
import com.yixuetang.entity.request.homework.InsertHomework;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.homework.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Colin
 * @version 1.0.0
 * @description 作业模块控制层
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
        return this.homeworkService.findByCourseId(courseId, userId);
    }

    @Override
    @PostMapping("courseId/{courseId}/{teacherId}")
    public CommonResponse saveHomework(@PathVariable long courseId, @PathVariable long teacherId, @RequestBody InsertHomework insertHomework) {
        return this.homeworkService.saveByCourseId(courseId, teacherId, insertHomework);
    }

    @Override
    @DeleteMapping("id/{homeworkId}/{courseId}")
    public CommonResponse deleteHomework(@PathVariable long homeworkId,  @PathVariable long courseId) {
        return this.homeworkService.deleteByHomeworkId(homeworkId, courseId);
    }

    @Override
    @GetMapping("homeworkId/{homeworkId}")
    public QueryResponse findById(@PathVariable long homeworkId) {
        return this.homeworkService.findById( homeworkId );
    }

    @Override
    @PutMapping("info/homeworkId/{homeworkId}")
    public CommonResponse updateHomework(@PathVariable long homeworkId, @RequestBody InsertHomework insertHomework) {
        return this.homeworkService.updateHomework(homeworkId, insertHomework);
    }

    @Override
    @PutMapping("topNum/homeworkId/{homeworkId}")
    public CommonResponse switchTop(@PathVariable long homeworkId) {
        return this.homeworkService.switchTopNum( homeworkId );
    }

}
