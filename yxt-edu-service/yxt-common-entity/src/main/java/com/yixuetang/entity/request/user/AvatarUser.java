package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AvatarUser {
    private String realName; // 真实姓名

    @ApiModelProperty(value = "头像地址", required = true, dataType = "String")
    private String avatar; // 头像
}
