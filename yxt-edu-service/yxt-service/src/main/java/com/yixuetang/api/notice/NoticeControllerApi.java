package com.yixuetang.api.notice;

import com.yixuetang.entity.request.notice.InsertNotice;
import com.yixuetang.entity.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 公告模块下的作业api
 * @date 2020/11/12 12:19
 */
@Api(value = "公告管理接口", description = "公告管理接口，提供作业的增、删、改、查")
public interface NoticeControllerApi {

    @ApiOperation("新增公告")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "teacherId", value = "教师id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse saveNotice(long courseId, long teacherId, InsertNotice insertNotice);

    @ApiOperation("删除公告")
    @ApiImplicitParam(name = "noticeId", value = "公告id", required = true,
                    paramType = "path", dataType = "long")
    CommonResponse deleteNotice(long noticeId);

    @ApiOperation("编辑公告")
    @ApiImplicitParam(name = "noticeId", value = "公告id", required = true,
            paramType = "path", dataType = "long")
    CommonResponse updateNotice(long noticeId,InsertNotice insertNotice);

}
