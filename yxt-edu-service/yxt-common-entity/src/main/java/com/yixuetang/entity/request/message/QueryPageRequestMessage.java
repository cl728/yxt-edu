package com.yixuetang.entity.request.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/24 19:40
 */
@Data
public class QueryPageRequestMessage {

    @ApiModelProperty(value = "接收者类型 全部 全部用户 单一用户", dataType = "String")
    private String receiverType;

    @ApiModelProperty(value = "通知状态 全部 拉取 未拉取", dataType = "String")
    private String status;

    @ApiModelProperty(value = "通知标题", dataType = "String")
    private String title;

    @ApiModelProperty(hidden = true)
    private Integer type;

    @ApiModelProperty(hidden = true)
    private Boolean messageStatus;

}
