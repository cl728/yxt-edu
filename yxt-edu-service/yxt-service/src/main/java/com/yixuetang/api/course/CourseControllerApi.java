package com.yixuetang.api.course;

import com.yixuetang.entity.request.course.InsertCourse;
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

    @ApiOperation("查询所有课程")
    QueryResponse findAllCourses();

    @ApiOperation("教师删除课程/学生退课")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "courseId", value = "课程主键id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse deleteCourse(Long userId, Long courseId);

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
    QueryResponse findByPage(long currentPage, long pageSize);

    @ApiOperation("用户查询加入的课程列表")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findByUserId(long userId);

    @ApiOperation("教师查询创建的课程列表")
    @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findByTeacherId(long teacherId);

    @ApiOperation("用户置顶加入的课程/取消置顶")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "isTop", value = "是否置顶", required = true,
                    paramType = "path", dataType = "boolean")
    })
    CommonResponse updateTopSCourse(long courseId,long userId,boolean isTop);

    @ApiOperation("教师置顶创建的课程/取消置顶")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "isTop", value = "是否置顶", required = true,
                    paramType = "path", dataType = "boolean")
    })
    CommonResponse updateTopTCourse(long courseId,long teacherId,boolean isTop);

}
