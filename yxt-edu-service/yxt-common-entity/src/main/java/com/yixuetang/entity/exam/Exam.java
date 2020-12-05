package com.yixuetang.entity.exam;

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

    @TableField("question_count")
    private Integer questionCount;  // 测试包含的题目数量，默认为0

    @TableField("total_score")
    private Double totalScore;  // 测试的总分

    private Integer status;     // 测试的状态，0 未发布，1 未开始，2 已发布，3 已结束

    @TableField("top_num")
    private Integer topNum;     // 测试置顶字段，数字越大，置顶等级越高，默认为0

}
