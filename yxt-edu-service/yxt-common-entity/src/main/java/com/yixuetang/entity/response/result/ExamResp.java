package com.yixuetang.entity.response.result;

import com.yixuetang.entity.exam.Exam;
import com.yixuetang.entity.homework.Homework;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/6 14:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamResp extends Exam {

    private Integer uncommittedCount;	// 未提交人数
    private Integer uncorrectedCount;	// 待批改人数
    private Integer correctedCount;		// 已批改人数
    private Integer status;			    // 学生的作业完成情况

}
