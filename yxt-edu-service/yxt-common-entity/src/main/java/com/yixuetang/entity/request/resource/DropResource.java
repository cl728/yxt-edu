package com.yixuetang.entity.request.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 拖拽资源实体类
 * @date 2020/11/28 13:14
 */
@Data
public class DropResource {

    @ApiModelProperty(value = "要拖拽到的目标资源id", required = true, dataType = "long")
    private long dropId;

    @ApiModelProperty(value = "拖拽到目标资源的位置 before inner after", required = true, dataType = "String")
    private String dropType;

}
