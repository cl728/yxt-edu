package com.yixuetang.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.notice.NoticeUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
