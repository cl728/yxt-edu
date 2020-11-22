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
     * @return 响应结果实体类
     */
    QueryResponse findTopCommentsByNoticeId(long noticeId);

    /**
     * 在某一公告下发布评论
     *
     * @param noticeId 公告id
     * @param userId   用户id
     * @return
     */
    CommonResponse postCommentToNotice(long noticeId, long userId, PostComment postComment);
}
