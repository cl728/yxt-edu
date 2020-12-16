package com.yixuetang.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 作业-学生实体类
 * @date 2020/11/10 19:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_homework_student")
public class HomeworkStudent {

    @TableId(type = IdType.AUTO)
    private Long id;    // 主键自增id

    @TableField("homework_id")
    private Long homeworkId; // 作业id

    @TableField("student_id")
    private Long studentId; // 学生id

    @TableField("score")
    private Double score; // 作业分数

    @TableField("submit_time")
    private Date submitTime; // 首次提交时间

    @TableField("update_time")
    private Date updateTime; // 最后一次提交时间

    private Integer status; // 作业状态 0待完成 1待批改 2已批改 3被打回

    @TableField("correct_count")
    private Integer correctCount; // 教师批阅次数

    private String message; // 教师批语

}
