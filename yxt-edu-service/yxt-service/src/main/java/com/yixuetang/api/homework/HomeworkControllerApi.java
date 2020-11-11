package com.yixuetang.api.homework;

import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程模块下的作业api
 * @date 2020/11/11 16:46
 */
@Api(value = "作业管理接口", description = "作业管理接口，提供作业的增、删、改、查")
public interface HomeworkControllerApi {

    @ApiOperation("查询某门课程下的作业")
    @ApiImplicitParams( value = {
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findByCourseId(long courseId, long userId);

}
