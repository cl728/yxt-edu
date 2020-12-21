package com.yixuetang.entity.request.course;

import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/20 19:05
 */
@Data
public class EditStudentUsualScoreRequest {

    private Long courseId;

    private Long studentId;

    private Double studentScore;

}
