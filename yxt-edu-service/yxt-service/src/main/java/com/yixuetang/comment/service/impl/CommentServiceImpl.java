package com.yixuetang.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.comment.mapper.CommentMapper;
import com.yixuetang.comment.service.CommentService;
import com.yixuetang.entity.comment.Comment;
import com.yixuetang.entity.notice.Notice;
import com.yixuetang.entity.request.comment.PostComment;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.comment.CommentCode;
import com.yixuetang.entity.response.code.notice.NoticeCode;
import com.yixuetang.entity.response.code.user.UserCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.mq.AmqpUtils;
import com.yixuetang.notice.mapper.NoticeMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpUtils amqpUtils;

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

    /**
     * 在某一公告下发布评论
     *
     * @param noticeId 公告id
     * @param userId   用户id
     * @return
     */
    @Transactional
    @Override
    public CommonResponse postCommentToNotice(long noticeId, long userId, PostComment postComment) {

        // 1.参数验证
        if (postComment == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 根据公告id查询出已有的公告信息
        Notice notice = this.noticeMapper.findById( noticeId );
        if (notice == null) {
            //找不到公告
            return new CommonResponse( NoticeCode.NOTICE_NOT_FOUND );
        }

        // 3. 根据用户id查询出已有的用户信息
        User user = this.userMapper.findById( userId );
        if (user == null) {
            //找不到用户
            return new CommonResponse( UserCode.USER_NOT_FOUND );
        }

        // 4. 发布评论
        Comment comment = Comment.builder().id( null )
                .content( postComment.getContent() )
                .createTime( new Date() ).build();
        this.commentMapper.insert( comment );

        // 5. 关联 parentCommentId
        if (postComment.getParentCommentId() != -1) {
            this.commentMapper.updateParentIdById( postComment.getParentCommentId(), comment.getId() );

            Long receiverId = this.commentMapper.findById( postComment.getParentCommentId() ).getUser().getId();
            if (!ObjectUtils.nullSafeEquals( userId, receiverId )) {
                // 发送异步事件提醒，告知被回复者有人回复了他的评论
                this.amqpUtils.sendReplyRemind( userId, receiverId, comment.getId(), postComment.getParentCommentId(),
                        "http://www.yixuetang.com/noticeDetail.html?id=" + noticeId );
            }
        }

        // 6. 关联 noticeId 和 userId
        this.commentMapper.updateNoticeIdAndUserIdById( noticeId, userId, comment.getId() );

        return CommonResponse.SUCCESS();
    }

    /**
     * 在某一公告下删除评论
     *
     * @param commentId 评论id
     * @return 响应结果实体类
     */
    @Transactional
    @Override
    public CommonResponse deleteComment(long commentId) {

        // 1. 根据评论id查询出已有的评论信息
        Comment comment = this.commentMapper.findById( commentId );
        if (comment == null) {
            //找不到该评论
            return new CommonResponse( CommentCode.COMMENT_NOT_FOUND );
        }

        // 2. 判断该评论是否存在子评论
        List<Comment> childComments = comment.getChildComments();
        if (!CollectionUtils.isEmpty( childComments )) {
            // 先删除子评论
            childComments.forEach( childComment -> this.deleteComment( childComment.getId() ) );
        }

        // 3. 再根据评论id删除此评论
        this.commentMapper.deleteById( commentId );

        return new CommonResponse( CommonCode.SUCCESS );
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
