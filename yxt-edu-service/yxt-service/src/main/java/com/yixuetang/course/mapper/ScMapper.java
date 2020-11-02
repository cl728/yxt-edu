package com.yixuetang.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.course.StudentCourse;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

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
     * @param studentId 学生主键id
     * @param courseId  课程主键id
     * @return 操作影响行数
     */
    @Select("select count(*) from t_sc where student_id = #{studentId} and course_id = #{courseId}")
    int selectByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 按置顶字段排序查找用户课程信息
     * @param userId
     * @return
     */
    @Results(id = "scMap", value = {
            @Result(column = "course_id", property = "course",
                    one = @One(select = "com.yixuetang.course.mapper.CourseMapper.findById", fetchType = FetchType.EAGER))
    })
    @Select("SELECT * FROM t_sc where student_id=#{userId} ORDER BY top_num DESC")
    List<StudentCourse> selectCourseByUserId(@Param("userId") Long userId);

    /**
     * 修改置顶字段
     *
     * @param studentId 学生主键id
     * @param courseId  课程主键id
     * @return 操作影响行数
     */
    @Update("update t_sc set top_num=#{topNum} where student_id = #{studentId} and course_id = #{courseId}")
    void updateTopNumByStudentIdAndCourseId(@Param("topNum") int topNum,@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("SELECT MAX(top_num) FROM t_sc WHERE student_id=#{studentId}")
    int selectMaxTopByStudentId(@Param("studentId") Long studentId);


}
