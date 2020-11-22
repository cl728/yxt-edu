package com.yixuetang.api.comment;

import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Colin
 * @version 1.0.0
 * @description 评论模块的api
 * @date 2020/11/20 21:49
 */
@Api(value = "评论模块管理接口", description = "评论模块管理接口，提供评论的增、删、改、查")
public interface CommentControllerApi {

    @ApiOperation("查询某一公告下的顶级评论列表")
    @ApiImplicitParam(name = "noticeId", value = "公告id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findTopCommentsByNoticeId(long noticeId);

}
