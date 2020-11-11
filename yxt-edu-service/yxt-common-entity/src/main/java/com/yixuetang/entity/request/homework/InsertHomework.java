package com.yixuetang.entity.request.homework;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 添加作业的实体类
 * @date 2020/11/11 21:24
 */
@Data
public class InsertHomework {

    @ApiModelProperty(value = "作业标题", required = true, dataType = "String")
    private String title;

    @ApiModelProperty(value = "作业描述", required = true, dataType = "String")
    private String description;

    @ApiModelProperty(value = "截止时间", required = true, dataType = "String")
    private Date deadline;

    @ApiModelProperty(value = "总分值", required = true, dataType = "String")
    private Double totalScore;

}
