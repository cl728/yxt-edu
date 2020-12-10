package com.yixuetang.entity.homework;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 教师评分作业实体类
 * @date 2020/12/9 22:53
 */
@Data
public class ScoreStudentHomework {

    @ApiModelProperty(value = "分数", required = true, dataType = "double")
    private double score;

    @ApiModelProperty(value = "教师批语", dataType = "String")
    private String message;

}
