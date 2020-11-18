package com.yixuetang.entity.request.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/18 9:40
 */
@Data
public class InsertCourseResource {

    @ApiModelProperty(name = "课程id", dataType = "long")
    private Long courseId;

    @ApiModelProperty(name = "资源id", dataType = "long")
    private Long resourceId;

}
