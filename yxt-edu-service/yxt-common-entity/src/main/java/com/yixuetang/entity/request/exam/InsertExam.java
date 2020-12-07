package com.yixuetang.entity.request.exam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Hemon
 * @version 1.0
 * @date 2020/12/7 16:33
 * @description 新建测试的实体类
 */
@Data
public class InsertExam {

    @ApiModelProperty(value = "测试标题", required = true, dataType = "String")
    private String title;

    @ApiModelProperty(value = "测试简介", required = true, dataType = "String")
    private String introduction;

    @ApiModelProperty(value = "测试开始时间", required = true, dataType = "String")
    private Date startTime;

    @ApiModelProperty(value = "测试结束时间", required = false, dataType = "String")
    private Date endTime;
}
