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
 * @description 学生试卷表，记录学生跟整张试卷的信息
 * @date 2020/12/5 14:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_exam_student")
public class ExamStudent {

    @TableId(type = IdType.AUTO)
    private Long id;            // 主键自增id

    @TableField("exam_id")
    private Long examId;        // 测试（考试）id

    @TableField("student_id")
    private Long studentId;     // 学生id

    private Double score;       // 学生得分

    private Integer status;     // 测试（考试）状态 0待完成 1待批改 2已批改

    @TableField("submit_time")
    private Date submitTime;    // 提交测试（考试）时间

}
