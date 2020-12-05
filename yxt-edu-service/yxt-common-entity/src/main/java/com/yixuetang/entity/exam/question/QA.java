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
 * @description 问答题表 Q & A
 * @date 2020/12/5 13:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_qa")
public class QA {

    @TableId(type = IdType.AUTO)
    private Long id;            // 主键自增id

    @TableField("teacher_id")
    private Long teacherId;     // 出题教师id

    private String content;     // 题目内容

    private String answer;      // 参考答案，如果是多空填空题，则每个空的答案以英文逗号隔开

    private String analysis;    // 试题解析

    private Double score;       // 分值

    @TableField("create_time")
    private Date createTime;    // 创建时间

}
