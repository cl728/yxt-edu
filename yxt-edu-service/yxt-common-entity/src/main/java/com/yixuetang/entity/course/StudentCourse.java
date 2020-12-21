package com.yixuetang.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Hemon
 * @version 1.0
 * @date 2020/10/30 11:04
 * @description 课程模块课程-学生实体类
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_student_course")
public class StudentCourse {

    @TableId(type = IdType.AUTO)
    private Long id; // SC主键自增id

    @TableField("student_id")
    private Long studentId; // 学生主键id

    @TableField("course_id")
    private Long courseId; // 课程主键id

    @TableField("final_grade")
    private Double finalGrade;  // 最终成绩

    @TableField("top_num")
    private Integer topNum; // 置顶字段

    @TableField("is_filed")
    private Boolean isFiled; // 是否被归档，true 是 false 不是

    @TableField("join_time")
    private Date joinTime;  // 加课时间

    @TableField("usual_score")
    private Double usualScore;  // 平时成绩

    @TableField(exist = false)
    private Course course;

    @TableField(exist = false)
    private User user;

}
