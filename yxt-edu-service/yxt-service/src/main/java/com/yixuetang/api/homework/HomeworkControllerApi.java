package com.yixuetang.api.homework;

import com.yixuetang.entity.request.homework.InsertHomework;
import com.yixuetang.entity.request.homework.SubmitHomework;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Colin
 * @version 1.0.0
 * @description 作业模块api
 * @date 2020/11/11 16:46
 */
@Api(value = "作业管理接口", description = "作业管理接口，提供作业的增、删、改、查")
public interface HomeworkControllerApi {

    @ApiOperation("查询某门课程下的全部作业成绩信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findScoresByCourseId(long courseId, long teacherId);

    @ApiOperation("查询某门课程下的作业")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findByCourseId(long courseId, long userId);

    @ApiOperation("新增作业")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse saveHomework(long courseId, long teacherId, InsertHomework insertHomework);

    @ApiOperation("教师删除作业")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse deleteHomework(long homeworkId, long courseId);

    @ApiOperation("根据作业id查询作业信息")
    @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findById(long homeworkId);

    @ApiOperation("教师编辑作业")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse updateHomework(long homeworkId, InsertHomework insertHomework);

    @ApiOperation("根据作业id切换置顶字段")
    @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse switchTop(long homeworkId);

    @ApiOperation("学生提交作业")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "studentId", value = "学生id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse submitHomework(long homeworkId, long studentId, SubmitHomework submitHomework);

    @ApiOperation("学生作业评分")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "studentId", value = "学生id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse scoreHomework(long homeworkId, long studentId, double score);

    @ApiOperation("查询某个学生某次作业的提交情况")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "homeworkId", value = "作业id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "studentId", value = "学生id", required = true,
                    paramType = "path", dataType = "long"),
    })
    QueryResponse findStudentHomeworkSubmit(long homeworkId, long studentId);

    @ApiOperation("查询某个学生在某个课程下的作业完成情况")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "studentId", value = "学生id", required = true,
                    paramType = "path", dataType = "long"),
    })
    QueryResponse findStudentCourseHomework(long courseId, long studentId);
}
