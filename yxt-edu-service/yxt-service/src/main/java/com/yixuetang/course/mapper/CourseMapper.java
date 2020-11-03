package com.yixuetang.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.entity.course.Course;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

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

    @Results(id = "courseMap", value = {
            @Result(column = "class", property = "clazz"),
            @Result(column = "teacher_id", property = "teacher",
                    one = @One(select = "com.yixuetang.user.mapper.UserMapper.findById", fetchType = FetchType.EAGER))
    })
    @Select("select id, teacher_id, c_name, class, school_year, semester, c_code, s_count from t_course")
    List<Course> findByPage(Page<Course> page);

    @ResultMap("courseMap")
    @Select("select id, teacher_id from t_course where c_code = #{code}")
    Course findByCCode(String code);

    @Update("update t_course set teacher_id = #{teacherId} where c_code = #{cCode}")
    void updateTeacherIdByCCode(@Param("teacherId") Long teacherId, @Param("cCode") String cCode);
}
