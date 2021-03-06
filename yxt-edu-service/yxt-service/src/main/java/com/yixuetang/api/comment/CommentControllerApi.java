package com.yixuetang.api.comment;

import com.yixuetang.entity.request.comment.PostComment;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author Colin
 * @version 1.0.0
 * @description 评论模块的api
 * @date 2020/11/20 21:49
 */
@Api(value = "评论模块管理接口", description = "评论模块管理接口，提供评论的增、删、改、查")
public interface CommentControllerApi {

    @ApiOperation("管理员删除某个评论")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "commentId", value = "评论id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse deleteComment(long commentId);

    @ApiOperation("在某一公告下发布评论")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "noticeId", value = "公告id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse postCommentToNotice(long noticeId, long userId, PostComment postComment);

    @ApiOperation("查询某一公告下的顶级评论列表")
    @ApiImplicitParam(name = "noticeId", value = "公告id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findTopCommentsByNoticeId(long noticeId);

    @ApiOperation("用户给评论点赞/取消点赞")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "commentId", value = "评论id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
    })
    CommonResponse likeComment(long commentId, long userId);

    @ApiOperation("查询某⼀⽤⼾对评论的点赞状态")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findLike(long userId);

    @ApiOperation("查询评论的点赞数量")
    QueryResponse findCommentVoteUpCount();

    @ApiOperation("分页查询评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页码数", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true,
                    paramType = "path", dataType = "long")
    })
    QueryResponse findCommentByPage(long currentPage, long pageSize);
}
