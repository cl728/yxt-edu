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

    @Select("select id, title, description, deadline, total_score from t_homework where id = #{id}")
    @Results(id = "homeworkMap", value = {
            @Result(column = "id", property = "studentList",
                    many = @Many(select = "com.yixuetang.user.mapper.UserMapper.findByHomeworkId", fetchType = FetchType.LAZY))
    })
    Homework findById(Long id);

}
