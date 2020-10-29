package com.yixuetang.api.course;

import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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

    @ApiOperation("删除一门课程")
    @ApiImplicitParam(name = "id", value = "课程主键id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse deleteCourse(Long id);
}