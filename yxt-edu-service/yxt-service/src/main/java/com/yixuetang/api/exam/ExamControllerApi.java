package com.yixuetang.api.exam;

import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.InsertQuestion;
import com.yixuetang.entity.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块的api
 * @date 2020/12/07 10:21
 */
@Api(value = "考试模块管理接口", description = "考试模块管理接口，提供考试的增、删、改、查")
public interface ExamControllerApi {
    @ApiOperation("教师在某一课程下新建测试（考试）")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse newExam(long courseId, InsertExam insertExam);

    @ApiOperation("教师置顶/取消置顶测试（考试）")
    @ApiImplicitParam(name = "examId", value = "测试（考试）id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse examTop(long examId);

    @ApiOperation("教师保存题目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "测试（考试）id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "出题教师id", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse saveQuestion(long examId, long teacherId, InsertQuestion insertQuestion);

}
