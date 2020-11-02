package com.yixuetang.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.course.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块课程实体类持久层接口
 * @date 2020/10/29 11:22
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    @Select("select * from t_course where id = #{id}")
    Course findById(Long id);
}
