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
 * @description 试卷信息表，记录试卷包含的试题记录
 * @date 2020/12/5 13:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_exam_question")
public class ExamQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键自增id

    @TableField("exam_id")
    private Long examId;                // 测试（考试）id

    @TableField("question_number")
    private Integer questionNumber;     // 试题编号

    @TableField("question_type")
    private Integer questionType;       // 试题类型，0选择题，1判断题，2填空题，3问答题

    @TableField("question_id")
    private Long questionId;            // 试题id

}
