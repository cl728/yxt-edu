package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/10/25 17:03
 */
@Data
public class EmailUser {

    @ApiModelProperty("邮箱地址")
    private String email;

    @ApiModelProperty(value = "验证码", required = true, dataType = "String")
    private String code;

    @ApiModelProperty(value = "最后一次更新个人信息时间", dataType = "date")
    private Date updateTime;

}
