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
 * @description 选择题表
 * @date 2020/12/5 11:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_select")
public class Select {

    @TableId(type = IdType.AUTO)
    private Long id;            // 主键自增id

    @TableField("teacher_id")
    private Long teacherId;     // 出题教师id

    private Integer type;       // 题目类型，0 单选题，1 多选题

    private String content;     // 题目内容

    private String choices;     // 选项列表的JSON字符串

    private String answer;      // 参考答案，如果是单选题，则答案为A、B、C 或 D，如果是多选题，则答案为 A、B、C、D 的组合，并以英文逗号隔开

    private String analysis;    // 试题解析

    private Double score;       // 分值

    @TableField("create_time")
    private Date createTime;    // 创建时间

}
