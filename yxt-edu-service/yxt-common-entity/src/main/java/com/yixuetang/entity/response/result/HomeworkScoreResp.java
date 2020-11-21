package com.yixuetang.entity.response.result;

import com.yixuetang.entity.homework.HomeworkStudent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Curtis
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/20 22:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeworkScoreResp extends HomeworkStudent {

    private String realName; //学生姓名
    private String avatar;  // 头像
    private String tsNo;    // 学号
    private String title;   // 作业标题
    private Double totalScore; // 满分值

}
