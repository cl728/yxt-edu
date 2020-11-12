package com.yixuetang.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.notice.Notice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectKey;

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
}
