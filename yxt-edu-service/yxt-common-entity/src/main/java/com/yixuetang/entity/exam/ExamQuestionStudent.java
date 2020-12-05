package com.yixuetang.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description 试卷试题学生表，记录学生做的试卷试题情况
 * @date 2020/12/5 14:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_exam_question_student")
public class ExamQuestionStudent {

    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键自增id

    @TableField("exam_question_id")
    private Long examQuestionId;        // 试卷试题id

    @TableField("student_id")
    private Long studentId;             // 学生id

    @TableField
    private String answer;              // 学生提交的答案

    @TableField
    private Double score;               // 学生该题的得分

}
