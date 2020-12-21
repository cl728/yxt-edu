package com.yixuetang.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.course.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 测试（考试）实体类
 * @date 2020/12/5 10:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_exam")
public class Exam {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("course_id")
    private Long courseId;  // 关联的课程id

    private String title;   // 测试标题

    private String introduction;    // 测试简介

    @TableField("create_time")
    private Date createTime;    // 创建时间

    @TableField("start_time")
    private Date startTime;     // 测试开始时间

    @TableField("end_time")
    private Date endTime;       // 测试结束时间

    @TableField(exist = false)
    private Integer questionCount;  // 测试包含的题目数量

    @TableField(exist = false)
    private Double totalScore;  // 测试的总分

    @TableField(exist = false)
    private Course course;      // 关联的课程

    private Boolean status;     // 测试是否已发布，true 是 false 不是，默认为 false

    @TableField("top_num")
    private Integer topNum;     // 测试置顶字段，数字越大，置顶等级越高，默认为0

    @TableField("final_exam")
    private Boolean finalExam;  // 是否为期末测试，true 是 false 不是

}
