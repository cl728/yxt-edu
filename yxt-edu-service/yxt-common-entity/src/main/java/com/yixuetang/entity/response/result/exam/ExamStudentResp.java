package com.yixuetang.entity.response.result.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/20 15:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStudentResp {

    private String title;

    private Double studentScore;

    private Double totalScore;

    private Integer status;

    private Boolean finalExam;

}
