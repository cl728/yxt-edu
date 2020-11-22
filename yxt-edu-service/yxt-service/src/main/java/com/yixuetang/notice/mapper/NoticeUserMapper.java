package com.yixuetang.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.notice.NoticeUser;
import com.yixuetang.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Curtis
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/12 12:46
 */
@Mapper
public interface NoticeUserMapper extends BaseMapper<NoticeUser> {

    @Select("select count(user_id) from t_nu where notice_id = #{noticeId} and view = true")
    long findViewsByNoticeId(@Param("noticeId") long noticeId);

    @Select( "select id, avatar, real_name, ts_no from t_user where id in " +
            "(select user_id from t_nu where notice_id = #{noticeId} and view = true)" )
    List<User> findReadUsersByNoticeId(long noticeId);

    @Select( "select id, avatar, real_name, ts_no from t_user where id in " +
            "(select user_id from t_nu where notice_id = #{noticeId} and view = false)" )
    List<User> findUnreadUsersByNoticeId(long noticeId);
}
