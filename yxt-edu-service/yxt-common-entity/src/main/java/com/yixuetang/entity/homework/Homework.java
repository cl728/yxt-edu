package com.yixuetang.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.user.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 作业实体类
 * @date 2020/11/10 17:28
 */
@Data
@TableName("t_homework")
public class Homework {

    @TableId(type = IdType.AUTO)
    private Long id; // 作业主键自增id

    @TableField("course_id")
    private Long courseId;  // 课程id

    private String title;   // 作业标题

    private String description; // 作业描述

    @TableField("create_time")
    private Date createTime;    // 发布时间

    private Date deadline;  // 截止提交时间

    @TableField("submit_count")
    private Integer submitCount; // 已提交人数

    @TableField("total_score")
    private Double totalScore; // 满分值

    @TableField("top_num")
    private Integer topNum; // 置顶字段，0代表不置顶

    @TableField(exist = false)
    private List<User> studentList; // 作业-学生 多对多

    @TableField(exist = false)
    private Course course;  // 作业-课程 多对一

}
