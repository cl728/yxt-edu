package com.yixuetang.entity.response.result.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/20 14:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStudentScoreResp {

    private String tsNo;

    private String realName;

    private List<ExamStudentResp> examStudentRespList;

}
