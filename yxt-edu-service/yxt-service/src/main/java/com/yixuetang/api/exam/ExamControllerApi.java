package com.yixuetang.api.exam;

import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.question.ExamQuestionRequest;
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
 * @date 2020/12/07 10:21
 */
@Api(value = "考试模块管理接口", description = "考试模块管理接口，提供考试的增、删、改、查")
public interface ExamControllerApi {

    @ApiOperation("教师发布/重新发布/取消发布测试（考试）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "测试（考试）id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "actionType", value = "操作类型，0⾸次发布 1重新发布 2取消发布 3结束发布，路径变量 ", required = true,
                    paramType = "path", dataType = "int")
    })
    CommonResponse updateStatus(long examId, long teacherId, int actionType);

    @ApiOperation("查询某一考试（测试）的基本信息")
    @ApiImplicitParam(name = "examId", value = "考试（测试）id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findExamById(long examId);

    @ApiOperation("教师删除某一考试（测试）")
    @ApiImplicitParam(name = "examId", value = "考试（测试）id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse deleteById(long examId);

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
    CommonResponse saveQuestion(long examId, long teacherId, ExamQuestionRequest examQuestionRequest);

    @ApiOperation("教师删除试卷的某道题目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "测试（考试）id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "questionNumber", value = "题目编号", required = true,
                    paramType = "path", dataType = "long")
    })
    CommonResponse deleteQuestion(long examId, long questionNumber);

    @ApiOperation("用户查询某门课程下的测试（考试）列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findListByCourseId(long courseId, long userId);

    @ApiOperation("修改某个测试（考试）的基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "测试（考试）id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse updateExam(long examId, InsertExam insertExam);

    @ApiOperation("用户查询某次测试（考试）下的试题列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examId", value = "测试（考试）id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findListByExamId(long examId, long userId);

}
