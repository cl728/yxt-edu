package com.yixuetang.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.exam.Exam;
import org.apache.ibatis.annotations.*;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/5 16:05
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    @Results(id = "ExamMap", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "course_id", property = "courseId"),
            @Result(column = "title", property = "title"),
            @Result(column = "introduction", property = "introduction"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "start_time", property = "startTime"),
            @Result(column = "end_time", property = "endTime"),
            @Result(column = "status", property = "status"),
            @Result(column = "top_num", property = "topNum"),
    })
    @Select("select * from t_exam where id = #{examId}")
    Exam findExamById(@Param("examId") long examId);

    @Update("update t_exam set status=#{exam.status},course_id=#{exam.courseId},title=#{exam.title},introduction=#{exam.introduction},create_time=#{exam.createTime},start_time=#{exam.startTime},end_time=#{exam.endTime},top_num=#{exam.topNum} where id=#{exam.id}")
    void updateStatus(@Param("exam") Exam exam);
}
