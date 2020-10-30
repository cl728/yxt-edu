package com.yixuetang.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.course.StudentCourse;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.user.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Hemon
 * @version 1.0
 * @date 2020/10/30 10:39
 * @description 课程模块SC实体类持久层接口
 */

@Mapper
public interface ScMapper extends BaseMapper<StudentCourse> {
    /**
     * 加入课程
     *
     * @param studentId 学生主键id
     * @param courseId  课程主键id
     */
    @Insert("insert into t_sc(student_id, course_id) value (#{studentId}, #{courseId})")
    void joinCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生id和课程id在SC表查找记录
     *
     * @param studentId
     * @param courseId
     * @return
     */
    @Select("select count(*) from t_sc where student_id = #{studentId} and course_id = #{courseId}")
    int selectByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

}
