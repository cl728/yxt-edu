package com.yixuetang.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.comment.mapper.CommentMapper;
import com.yixuetang.comment.mapper.CommentUserMapper;
import com.yixuetang.comment.service.CommentService;
import com.yixuetang.entity.comment.Comment;
import com.yixuetang.entity.comment.CommentUser;
import com.yixuetang.entity.comment.LikedStatusEnum;
import com.yixuetang.entity.message.CommonConstant;
import com.yixuetang.entity.notice.Notice;
import com.yixuetang.entity.request.comment.PostComment;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.comment.CommentCode;
import com.yixuetang.entity.response.code.notice.NoticeCode;
import com.yixuetang.entity.response.code.user.UserCode;
import com.yixuetang.entity.response.result.comment.CommentVoteUpCountResp;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.mq.AmqpUtils;
import com.yixuetang.notice.mapper.NoticeMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

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

    @Autowired
    private CommentUserMapper commentUserMapper;

    // 存放迭代找出的所有子代的集合
    private List<Comment> tempChildComments = new ArrayList<>();

    // 保存用户点赞数据的key
    private static final String MAP_KEY_USER_LIKED = "MAP_USER_LIKED";

    // 保存评论被点赞数量的key
    private static final String MAP_KEY_COMMENT_LIKED_COUNT = "MAP_COMMENT_LIKED_COUNT";

    @Resource(name = "template")
    private RedisTemplate redisTemplate;

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

    @Override
    @SuppressWarnings("unchecked")
    public CommonResponse likeComment(long commentId, long userId) {
        /*// 1. 根据评论id和用户id查询出询 t_comment_user 表的记录
        CommentUser commentUser = commentUserMapper.selectOne( new QueryWrapper<CommentUser>().eq( "comment_id", commentId )
                .eq( "user_id", userId ) );
        //  判断记录是否存在
        if (commentUser == null) {
            // 如果不存在，代表用户第一次给此评论点赞，新增一条记录，status 为 true
            this.commentUserMapper.insert(
                    CommentUser.builder()
                            .id( null )
                            .commentId( commentId )
                            .userId( userId )
                            .status( Boolean.TRUE ).build()
            );
            // 发送事件提醒，告知被点赞者有用户点赞了他的评论
            this.amqpUtils.sendVoteUpRemind( userId, commentId,
                    this.commentMapper.findById( commentId ).getUser().getId());
        } else {
            // 如果存在，切换点赞状态
            commentUser.setStatus( !commentUser.getStatus() );
            commentUserMapper.updateById( commentUser );
        }*/

        // filedKey 格式为：点赞用户id::被点赞评论id, filedValue 为 0（未点赞） 或 1（点赞）
        String filedKey = userId + "::" + commentId;
        Object filedValue = this.redisTemplate.opsForHash().get( MAP_KEY_USER_LIKED, filedKey );

        if (ObjectUtils.isEmpty( filedValue )) { // 没有相关记录，用户第一次给此评论点赞

            // 保存点赞记录到 redis 中
            this.redisTemplate.opsForHash().put( MAP_KEY_USER_LIKED, filedKey, LikedStatusEnum.LIKE.getCode() );

            // 将该被点赞评论的点赞数量 + 1
            this.redisTemplate.opsForHash().increment( MAP_KEY_COMMENT_LIKED_COUNT, String.valueOf( commentId ), 1 );

            // 发送事件提醒，告知被点赞者有用户点赞了他的评论
            Long posterId = this.commentMapper.findById( commentId ).getUser().getId();
            if (!Objects.equals( userId, posterId )) {  // 用户点赞本人评论不用提醒
                this.amqpUtils.sendVoteUpRemind( userId, commentId, posterId );
            }

        } else {    // 有此记录，切换点赞状态

            // 判断该名用户对此评论是否为点赞状态，true 是，false 不是
            boolean isLiked = Objects.equals( filedValue, LikedStatusEnum.LIKE.getCode() );

            // 更新该记录的点赞状态
            this.redisTemplate.opsForHash().put( MAP_KEY_USER_LIKED, filedKey,
                    isLiked ? LikedStatusEnum.UNLIKE.getCode() : LikedStatusEnum.LIKE.getCode() );

            // 更新被点赞评论的点赞数量
            this.redisTemplate.opsForHash().increment( MAP_KEY_COMMENT_LIKED_COUNT, String.valueOf( commentId ), isLiked ? -1 : 1 );

        }
        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @SuppressWarnings("unchecked")
    public QueryResponse findLike(long userId) {
        List<CommentUser> commentUsers = this.getLikedDataFromRedis(
                ScanOptions.scanOptions().count( 50L )
                        .match( userId + CommonConstant.REDIS_MATCH_PREFIX ) // 将该用户的点赞记录取出
                        .build() );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( commentUsers, commentUsers.size() ) );
    }

    @Override
    @Transactional
    public void transLikedFromRedis2DB() {
        List<CommentUser> commentUserList = this.getLikedDataFromRedis(
                ScanOptions.scanOptions().count( 50L ).build() );

        commentUserList.forEach( commentUser -> {

            CommentUser foundCommentUser = this.commentUserMapper.selectOne(
                    new QueryWrapper<>( CommentUser.builder()
                            .userId( commentUser.getUserId() )
                            .commentId( commentUser.getCommentId() ).build() ) );

            if (ObjectUtils.isEmpty( foundCommentUser )) { // 没有记录，直接存入
                this.commentUserMapper.insert( commentUser );
            } else { // 有记录，需要更新
                foundCommentUser.setStatus( commentUser.getStatus() );
                this.commentUserMapper.updateById( foundCommentUser );
            }

        } );
    }

    @Override
    @SuppressWarnings("unchecked")
    public QueryResponse findCommentVoteUpCount() {
        List<CommentVoteUpCountResp> commentVoteUpCountRespList = new ArrayList<>();
        Cursor<Map.Entry<Object, Object>> cursor =
                this.redisTemplate.opsForHash().scan( MAP_KEY_COMMENT_LIKED_COUNT,
                        ScanOptions.scanOptions().count( 100L ).build() );
        while (cursor.hasNext()) {

            Map.Entry<Object, Object> entry = cursor.next();
            String commentId = (String) entry.getKey();
            Integer commentCount = (Integer) entry.getValue();

            // 组装成 CommentVoteUpCountResp 对象
            CommentVoteUpCountResp commentVoteUpCountResp =
                    CommentVoteUpCountResp.builder()
                            .id( Long.parseLong( commentId ) )
                            .count( commentCount ).build();

            commentVoteUpCountRespList.add( commentVoteUpCountResp );

        }
        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( commentVoteUpCountRespList, commentVoteUpCountRespList.size() ) );
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

    @SuppressWarnings("unchecked")
    private List<CommentUser> getLikedDataFromRedis(ScanOptions scanOptions) {
        List<CommentUser> commentUserList = new ArrayList<>();
        Cursor<Map.Entry<Object, Object>> cursor =
                this.redisTemplate.opsForHash().scan( MAP_KEY_USER_LIKED, scanOptions );
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            String key = (String) entry.getKey();
            // 分离出 likedUserId，likedCommentId
            String[] split = key.split( "::" );
            String likedUserId = split[0];
            String likedCommentId = split[1];
            Integer value = (Integer) entry.getValue();

            // 组装成 CommentUser 对象
            CommentUser commentUser = CommentUser.builder()
                    .id( null )
                    .userId( Long.parseLong( likedUserId ) )
                    .commentId( Long.parseLong( likedCommentId ) )
                    .status( Objects.equals( LikedStatusEnum.LIKE.getCode(), value ) )
                    .build();
            commentUserList.add( commentUser );

        }
        return commentUserList;
    }
}
