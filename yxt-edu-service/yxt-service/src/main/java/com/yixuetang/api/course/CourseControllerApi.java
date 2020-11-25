package com.yixuetang.api.course;

import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.request.course.QueryPageRequestCourse;
import com.yixuetang.entity.request.course.TransferCourse;
import com.yixuetang.entity.request.course.UpdateCourse;
import com.yixuetang.entity.request.user.DelCourseUser;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块的api
 * @date 2020/10/29 11:11
 */
@Api(value = "课程模块管理接口", description = "课程模块管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {

    @ApiOperation("教师修改课程信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse updateCourses(Long courseId, Long teacherId, UpdateCourse updateCourse);

    @ApiOperation("根据id查询课程信息")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findById(long courseId);

    @ApiOperation("教师转让课程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse transferCourses(Long courseId, Long teacherId, TransferCourse transferCourse);


    @ApiOperation("查询所有课程")
    QueryResponse findAllCourses();

    @ApiOperation("教师删除课程/学生退课")
    @ApiImplicitParam(name = "courseId", value = "课程主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse deleteCourse(Long courseId, DelCourseUser delCourseUser);

    @ApiOperation("加入课程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "code", value = "加课码", required = true,
                    paramType = "path", dataType = "String")
    })
    CommonResponse joinCourse(Long userId, String code);

    @ApiOperation("添加课程")
    @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse saveCourse(Long teacherId, InsertCourse course);

    @ApiOperation("分页查询课程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findByPage(long currentPage, long pageSize, QueryPageRequestCourse queryPageRequestCourse);

    @ApiOperation("查询用户课程列表")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findCoursesByUserId(long userId);


    @ApiOperation("置顶课程/取消置顶")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse updateTopCourse(long courseId, long userId);

    @ApiOperation("归档")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse file(long courseId, long userId);

    @ApiOperation("管理员删除某门课程")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse delById(long courseId);

    @ApiOperation("重置加课码")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse resetCourseCode(long courseId);
}
