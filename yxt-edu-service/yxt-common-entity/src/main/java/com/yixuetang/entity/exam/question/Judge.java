package com.yixuetang.entity.exam.question;

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
 * @description 判断题表
 * @date 2020/12/5 11:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_judge")
public class Judge {

    @TableId(type = IdType.AUTO)
    private Long id;            // 主键自增id

    @TableField("teacher_id")
    private Long teacherId;     // 出题教师id

    private String content;     // 题目内容

    private Boolean answer;      // 参考答案，true / false

    private String analysis;    // 试题解析

    private Double score;       // 分值

    @TableField("create_time")
    private Date createTime;    // 创建时间
}
