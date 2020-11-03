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
    @ResultMap("courseMap")
    @Select("select * from t_course where id = #{id}")
    Course findById(Long id);

    @Results(id = "courseMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "c_name", property = "cName"),
            @Result(column = "c_pic", property = "cPic"),
            @Result(column = "c_code", property = "cCode"),
            @Result(column = "s_count", property = "sCount"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "school_year", property = "schoolYear"),
            @Result(column = "semester", property = "semester"),
            @Result(column = "class", property = "clazz"),
            @Result(column = "top_num", property = "topNum"),
            @Result(column = "is_filed", property = "isFiled"),
            @Result(column = "teacher_id", property = "teacher",
                    one = @One(select = "com.yixuetang.user.mapper.UserMapper.findById", fetchType = FetchType.EAGER))
    })
    @Select("select id, teacher_id, c_name, class, school_year, semester, c_code, s_count from t_course")
    List<Course> findByPage(Page<Course> page);

    @ResultMap("courseMap")
    @Select("select id, teacher_id, s_count from t_course where c_code = #{code}")
    Course findByCCode(String code);

    @Update("update t_course set teacher_id = #{teacherId} where c_code = #{cCode}")
    void updateTeacherIdByCCode(@Param("teacherId") Long teacherId, @Param("cCode") String cCode);

    @Update("update t_course set teacher_id = #{teacherId}, update_time = #{course.updateTime} where id = #{course.id}")
    void updateTeacherIdById(@Param("course") Course course, @Param("teacherId") Long teacherId);
}
