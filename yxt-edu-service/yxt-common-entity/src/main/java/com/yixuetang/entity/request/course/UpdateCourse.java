package com.yixuetang.entity.request.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 更新课程信息的实体类
 * @date 2020/11/03 09:40
 */
@Data
public class UpdateCourse {

    @ApiModelProperty(value = "课程名称", required = true, dataType = "String")
    private String cName;

    @ApiModelProperty(value = "班级名称", required = true, dataType = "String")
    private String clazz;

    @ApiModelProperty(value = "学年", required = true, dataType = "String")
    private String schoolYear;

    @ApiModelProperty(value = "学期", required = true, dataType = "String")
    private String semester;
}
