package com.yixuetang.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.comment.Comment;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/20 20:32
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select count(*) from t_comment where notice_id = #{noticeId}")
    long findCommentCountByNoticeId(long noticeId);

    @Results(id = "commentMap", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "content", property = "content"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "user_id", property = "user",
                    one = @One(select = "com.yixuetang.user.mapper.UserMapper.findCommentUserById", fetchType = FetchType.EAGER)),
            @Result(column = "notice_id", property = "notice",
                    one = @One(select = "com.yixuetang.notice.mapper.NoticeMapper.findById", fetchType = FetchType.EAGER)),
            @Result(column = "parent_comment_id", property = "parentComment",
                    one = @One(select = "com.yixuetang.comment.mapper.CommentMapper.findParentCommentById", fetchType = FetchType.EAGER)),
            @Result(column = "id", property = "childComments",
                    many = @Many(select = "com.yixuetang.comment.mapper.CommentMapper.findChildCommentsById", fetchType = FetchType.LAZY))
    })
    @Select("select id, content, create_time, user_id, parent_comment_id from t_comment " +
            " where notice_id = #{noticeId} and parent_comment_id is null " +
            " order by create_time")
    List<Comment> findTopCommentsByNoticeId(long noticeId);

    @Results({
            @Result(column = "user_id", property = "user",
                    one = @One(select = "com.yixuetang.user.mapper.UserMapper.findCommentUserById", fetchType = FetchType.EAGER))
    })
    @Select("select * from t_comment where id = #{parentCommentId}")
    Comment findParentCommentById(@Param("parentCommentId") long parentCommentId);

    @ResultMap("commentMap")
    @Select("select t1.id, t1.content, t1.create_time, t1.user_id, t1.parent_comment_id " +
            " from t_comment t1, t_comment t2 " +
            " where t1.parent_comment_id = t2.id and t2.id = #{id}")
    List<Comment> findChildCommentsById(long id);

  /*  @Insert("insert into t_comment(notice_id,user_id,content,parent_comment_id,create_time) values(#{comment.notice.id},#{comment.user.id},#{comment.content},#{comment.parentComment.id},#{comment.createTime})")
    void save(@Param("comment") Comment comment);*/

    @ResultMap("commentMap")
    @Select("select * from t_comment where id = #{id}")
    Comment findById(@Param("id") long id);

   /* @Select("select count(*) from t_comment_user where comment_id = #{commentId} and status = true")
    Integer findVoteUpCountById(long commentId);*/

    @Update("update t_comment set parent_comment_id = #{parentCommentId} where id = #{commentId}")
    void updateParentIdById(@Param("parentCommentId") long parentCommentId, @Param("commentId") Long commentId);

    @Update("update t_comment set notice_id = #{noticeId}, user_id = #{userId} where id = #{commentId}")
    void updateNoticeIdAndUserIdById(@Param("noticeId") long noticeId,
                                     @Param("userId") long userId,
                                     @Param("commentId") Long commentId);
}
