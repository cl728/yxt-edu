package com.yixuetang.entity.request.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 更新资源的实体类
 * @date 2020/11/28 10:41
 */
@Data
public class UpdateResource {

    @ApiModelProperty(value = "资源名称", required = true, dataType = "String")
    private String name;

}
