package com.yixuetang.entity.request.notice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 添加公告的实体类
 * @date 2020/11/11 21:24
 */
@Data
public class InsertNotice {

    @ApiModelProperty(value = "公告标题", required = true, dataType = "String")
    private String title;

    @ApiModelProperty(value = "公告内容", required = true, dataType = "String")
    private String content;

}
