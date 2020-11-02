package com.yixuetang.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
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
    private Long id; // SC主键自增id

    @TableField("student_id")
    private long studentId; // 学生主键id

    @TableField("course_id")
    private Long courseId; // 课程主键id

    @TableField("final_grade")
    private String finalGrade;  // 最终成绩

    @TableField("top_num")
    private Integer topNum; // 置顶字段

    @TableField("is_filed")
    private Boolean isFiled; // 是否被归档，true 是 false 不是
}
