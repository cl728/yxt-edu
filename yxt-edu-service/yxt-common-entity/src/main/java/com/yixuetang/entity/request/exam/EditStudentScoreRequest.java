package com.yixuetang.entity.request.exam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 修改学生某道题的得分请求实体类
 * @date 2020/12/15 18:59
 */
@Data
public class EditStudentScoreRequest {

    @ApiModelProperty(value = "测试（考试）id", required = true, dataType = "long")
    private Long examId;

    @ApiModelProperty(value = "题号", required = true, dataType = "int")
    private Integer questionNumber;

    @ApiModelProperty(value = "得分", required = true, dataType = "double")
    private Double studentScore;

    @ApiModelProperty(value = "学生id", required = true, dataType = "long")
    private Long studentId;

}
