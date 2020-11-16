package com.yixuetang.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.notice.Notice;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @author Curtis
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/12 12:27
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    @SelectKey(keyColumn = "id", keyProperty = "notice.id", before = false, resultType = Long.class, statement = "select last_insert_id()")
    @Insert("insert into t_notice values(#{notice.id},#{notice.course.id},#{notice.title},#{notice.content},#{notice.createTime},#{notice.updateTime})")
    void insertNotice(@Param("notice") Notice notice);

    @Results(id = "noticeMapper", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "title", property = "title"),
            @Result(column = "content", property = "content"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "course_id", property = "course",
                    one = @One(select = "com.yixuetang.course.mapper.CourseMapper.findById", fetchType = FetchType.EAGER)),
            @Result(column = "id", property = "views",
                    one = @One(select = "com.yixuetang.notice.mapper.NoticeUserMapper.findViewsByNoticeId", fetchType = FetchType.EAGER))
    })
    @Select("select * from t_notice where course_id = #{courseId}")
    List<Notice> findNoticeByCourseId(@Param("courseId") long courseId);
}
