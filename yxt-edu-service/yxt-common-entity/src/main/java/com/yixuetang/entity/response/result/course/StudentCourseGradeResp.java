package com.yixuetang.entity.response.result.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description 学生总成绩响应结果实体类
 * @date 2020/12/21 15:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseGradeResp {

    private String sno;

    private String studentName;

    private Double hwScore;

    private Double examScore;

    private Double usualScore;

    private Double finalScore;

}
