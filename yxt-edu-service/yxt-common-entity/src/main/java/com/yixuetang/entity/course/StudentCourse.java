package com.yixuetang.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Hemon
 * @version 1.0
 * @date 2020/10/30 11:04
 * @description 课程模块课程-学生实体类
 */

@Data
@TableName("t_sc")
public class StudentCourse {

    @TableId(type = IdType.AUTO)
    private long id; // SC主键自增id

    @TableField("student_id")
    private long studentId; // 学生主键id

    @TableField("course_id")
    private long courseId; // 课程主键id

    @TableField("final_grade")
    private String finalGrade;  // 最终成绩
}
