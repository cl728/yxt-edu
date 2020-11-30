package com.yixuetang.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.message.EventRemind;
import org.apache.ibatis.annotations.*;

/**
 * @author Colin
 * @version 1.0.0
 * @description 事件提醒持久层接口
 * @date 2020/11/25 13:37
 */
@Mapper
public interface RemindMapper extends BaseMapper<EventRemind> {

    @Results(id = "remindMap", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "remind_type", property = "remindType"),
            @Result(column = "sender_id", property = "senderId"),
            @Result(column = "course_id", property = "courseId"),
            @Result(column = "receiver_id", property = "receiverId"),
            @Result(column = "source_id", property = "sourceId"),
            @Result(column = "target_id", property = "targetId"),
            @Result(column = "action", property = "action"),
            @Result(column = "source_name", property = "sourceName"),
            @Result(column = "url", property = "url"),
            @Result(column = "status", property = "status"),
            @Result(column = "remind_time", property = "remindTime"),
    })
    @Select("select * from t_remind where id = #{id}")
    EventRemind findById(Long remindId);

    @Delete("delete from t_remind where id = #{id}")
    void deleteById(Long remindId);

}
