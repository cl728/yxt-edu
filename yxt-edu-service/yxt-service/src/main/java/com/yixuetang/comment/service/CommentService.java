package com.yixuetang.comment.service;

import com.yixuetang.entity.request.comment.PostComment;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/20 23:49
 */
public interface CommentService {

    /**
     * 查询某一公告下的顶级评论列表
     *
     * @param noticeId 公告id
     * @return 响带结果集的应结果实体类
     */
    QueryResponse findTopCommentsByNoticeId(long noticeId);

    /**
     * 在某一公告下发布评论
     *
     * @param noticeId 公告id
     * @param userId   用户id
     * @return 响应结果实体类
     */
    CommonResponse postCommentToNotice(long noticeId, long userId, PostComment postComment);

    /**
     * 管理员删除某个评论
     *
     * @param commentId 评论id
     * @return 响应结果实体类
     */
    CommonResponse deleteComment(long commentId);

    /**
     * 用户对评论点赞/取消点赞
     *
     * @param commentId 评论id
     * @param userId    用户id
     * @return 响应结果实体类
     */
    CommonResponse likeComment(long commentId, long userId);

    /**
     * 查询某一用户对评论的点赞状态
     *
     * @param userId 用户id
     * @return 响应结果实体类
     */
    QueryResponse findLike(long userId);

    /**
     * 将 Redis 里的点赞数据存入数据库中
     */
    void transLikedFromRedis2DB();
}
