package com.yixuetang.entity.response.result.exam;

import com.yixuetang.entity.exam.ExamStudent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/14 19:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamScoreResp extends ExamStudent {

    private String realName; // 学生姓名
    private String avatar;  // 头像
    private String tsNo;    // 学号
    private String title;   // 测试标题
    private Double totalScore; // 满分值

}
