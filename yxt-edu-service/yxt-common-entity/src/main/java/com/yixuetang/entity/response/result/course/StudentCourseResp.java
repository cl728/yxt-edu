package com.yixuetang.entity.response.result.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/20 18:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseResp {

    private Long studentId;

    private String tsNo;

    private String realName;

    private Double usualScore;

}
