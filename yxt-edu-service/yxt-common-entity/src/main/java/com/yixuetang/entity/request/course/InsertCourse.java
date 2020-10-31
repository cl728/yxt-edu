package com.yixuetang.entity.request.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 添加课程的实体类
 * @date 2020/10/31 15:16
 */
@Data
public class InsertCourse {

    @ApiModelProperty(value = "课程名称", required = true, dataType = "String")
    private String cName;

    @ApiModelProperty(value = "学年", required = true, dataType = "String")
    private String schoolYear;

    @ApiModelProperty(value = "学期", required = true, dataType = "String")
    private String semester;

    @ApiModelProperty(value = "班级", required = true, dataType = "String")
    private String clazz;

}
