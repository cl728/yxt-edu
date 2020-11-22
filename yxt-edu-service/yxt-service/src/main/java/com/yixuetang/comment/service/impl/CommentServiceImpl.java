package com.yixuetang.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.comment.mapper.CommentMapper;
import com.yixuetang.comment.service.CommentService;
import com.yixuetang.entity.comment.Comment;
import com.yixuetang.entity.notice.Notice;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.notice.mapper.NoticeMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/20 23:49
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    // 存放迭代找出的所有子代的集合
    private List<Comment> tempChildComments = new ArrayList<>();

    @Override
    public QueryResponse findTopCommentsByNoticeId(long noticeId) {
        if (this.noticeMapper.selectOne( new QueryWrapper<Notice>().eq( "id", noticeId ).select( "id" ) ) == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        List<Comment> topComments = this.commentMapper.findTopCommentsByNoticeId( noticeId );
        // 循环每个顶级评论，找出其子孙评论，并设置到其子级评论列表中
        List<Comment> comments = this.eachComment( topComments );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( comments, comments.size() ) );
    }

    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties( comment, c );
            commentsView.add( c );
        }
        // 合并评论的各层子代到第一级子代集合中
        this.combineChildren( commentsView );
        return commentsView;
    }

    private void combineChildren(List<Comment> comments) {

        for (Comment comment : comments) {
            List<Comment> replyComments = comment.getChildComments();
            for (Comment reply1 : replyComments) {
                // 循环迭代，找出子代，存放在 tempChildComments 中
                recursively( reply1, tempChildComments );
            }
            // 修改顶级节点的 childComments 为迭代处理后的集合
            comment.setChildComments( tempChildComments );
            // 清除临时存放区
            tempChildComments = new ArrayList<>();
        }
    }

    private void recursively(Comment comment, List<Comment> tempChildComments) {
        if (!tempChildComments.contains( comment )) {
            tempChildComments.add( comment ); // 顶节点添加到临时存放集合
        }
        List<Comment> childComments = comment.getChildComments();
        if (!CollectionUtils.isEmpty( childComments )) {
            childComments.forEach( childComment -> {
                tempChildComments.add( childComment );
                if (!CollectionUtils.isEmpty( childComment.getChildComments() )) {
                    this.recursively( childComment, tempChildComments );
                }
            } );
        }
    }
}
