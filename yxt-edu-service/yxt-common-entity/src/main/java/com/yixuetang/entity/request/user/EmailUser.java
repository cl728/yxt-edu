package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

}
