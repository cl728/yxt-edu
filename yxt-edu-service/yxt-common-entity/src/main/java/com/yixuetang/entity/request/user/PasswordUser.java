package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 用户密码修改实体类
 * @date 2020/10/28 9:32
 */
@Data
public class PasswordUser {

    @ApiModelProperty(value = "旧密码", required = true, dataType = "String")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true, dataType = "String")
    private String newPassword;

    @ApiModelProperty(value = "邮箱", required = true, dataType = "String")
    private String email;

    @ApiModelProperty(value = "验证码", required = true, dataType = "String")
    private String code;

    @ApiModelProperty(value = "最后一次更新个人信息时间", dataType = "date")
    private Date updateTime;
}
