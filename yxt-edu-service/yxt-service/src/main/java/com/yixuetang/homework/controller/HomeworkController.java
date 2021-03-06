package com.yixuetang.homework.controller;

import com.yixuetang.api.homework.HomeworkControllerApi;
import com.yixuetang.entity.homework.ScoreStudentHomework;
import com.yixuetang.entity.request.homework.InsertHomework;
import com.yixuetang.entity.request.homework.SubmitHomework;
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
    @GetMapping("scores/courseId/{courseId}/{teacherId}")
    public QueryResponse findScoresByCourseId(@PathVariable long courseId, @PathVariable long teacherId) {
        return this.homeworkService.findScoresByCourseId( courseId, teacherId );
    }

    @Override
    @GetMapping("courseId/{courseId}/{userId}")
    public QueryResponse findByCourseId(@PathVariable long courseId, @PathVariable long userId) {
        return this.homeworkService.findByCourseId( courseId, userId );
    }

    @Override
    @PostMapping("courseId/{courseId}/{teacherId}")
    public CommonResponse saveHomework(@PathVariable long courseId, @PathVariable long teacherId, @RequestBody InsertHomework insertHomework) {
        return this.homeworkService.saveByCourseId( courseId, teacherId, insertHomework );
    }

    @Override
    @DeleteMapping("id/{homeworkId}/{courseId}")
    public CommonResponse deleteHomework(@PathVariable long homeworkId, @PathVariable long courseId) {
        return this.homeworkService.deleteByHomeworkId( homeworkId, courseId );
    }

    @Override
    @GetMapping("homeworkId/{homeworkId}")
    public QueryResponse findById(@PathVariable long homeworkId) {
        return this.homeworkService.findById( homeworkId );
    }

    @Override
    @PutMapping("info/homeworkId/{homeworkId}")
    public CommonResponse updateHomework(@PathVariable long homeworkId, @RequestBody InsertHomework insertHomework) {
        return this.homeworkService.updateHomework( homeworkId, insertHomework );
    }

    @Override
    @PutMapping("topNum/homeworkId/{homeworkId}")
    public CommonResponse switchTop(@PathVariable long homeworkId) {
        return this.homeworkService.switchTopNum( homeworkId );
    }

    @Override
    @PostMapping("homeworkId/{homeworkId}/studentId/{studentId}")
    public CommonResponse submitHomework(@PathVariable long homeworkId, @PathVariable long studentId,
                                         @RequestBody SubmitHomework submitHomework) {
        return homeworkService.submitHomework( homeworkId, studentId, submitHomework.getResourceIds() );
    }

    @Override
    @PutMapping("homeworkId/{homeworkId}/studentId/{studentId}")
    public CommonResponse scoreHomework(@PathVariable long homeworkId, @PathVariable long studentId, @RequestBody ScoreStudentHomework scoreStudentHomework) {
        return homeworkService.scoreHomework( homeworkId, studentId, scoreStudentHomework );
    }

    @Override
    @GetMapping("homeworkId/{homeworkId}/studentId/{studentId}")
    public QueryResponse findStudentHomeworkSubmit(@PathVariable long homeworkId, @PathVariable long studentId) {
        return homeworkService.findStudentHomeworkSubmit( homeworkId, studentId );
    }

    @Override
    @GetMapping("courseId/{courseId}/studentId/{studentId}")
    public QueryResponse findStudentCourseHomework(@PathVariable long courseId, @PathVariable long studentId) {
        return homeworkService.findStudentCourseHomework( courseId, studentId );
    }

}
