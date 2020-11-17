package com.yixuetang.entity.request.resource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 创建文件夹的资源实体类
 * @date 2020/11/17 18:49
 */
@Data
public class InsertResource {

    @ApiModelProperty(name = "文件夹名称", dataType = "String")
    private String name;

}
