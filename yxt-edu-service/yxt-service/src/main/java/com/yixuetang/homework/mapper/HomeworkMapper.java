package com.yixuetang.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.homework.Homework;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/11 16:55
 */
@Mapper
public interface HomeworkMapper extends BaseMapper<Homework> {

    @Results(id = "homeworkMap", value = {
            @Result(column = "course_id", property = "course",
                    one = @One(select = "com.yixuetang.course.mapper.CourseMapper.findById", fetchType = FetchType.EAGER))
    })
    @Select("select id, course_id, title, description, create_time, deadline, submit_count, total_score" +
            " from t_homework where id = #{homeworkId}")
    Homework findById(long homeworkId);

}
