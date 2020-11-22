package com.yixuetang.comment.controller;

import com.yixuetang.api.comment.CommentControllerApi;
import com.yixuetang.comment.service.CommentService;
import com.yixuetang.entity.request.comment.PostComment;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/20 21:50
 */
@RestController
@RequestMapping("comment")
public class CommentController implements CommentControllerApi {

    @Autowired
    private CommentService commentService;

    @Override
    @PostMapping("/noticeId/{noticeId}/userId/{userId}")
    public CommonResponse postCommentToNotice(@PathVariable long noticeId, @PathVariable long userId, @RequestBody PostComment postComment) {
        return this.commentService.postCommentToNotice(noticeId, userId, postComment);
    }

    @Override
    @GetMapping("topComments/noticeId/{noticeId}")
    public QueryResponse findTopCommentsByNoticeId(@PathVariable long noticeId) {
        return this.commentService.findTopCommentsByNoticeId(noticeId);
    }

}
