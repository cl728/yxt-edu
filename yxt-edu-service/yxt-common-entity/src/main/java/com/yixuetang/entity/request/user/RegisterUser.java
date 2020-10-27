package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 注册用户实体类
 * @date 2020/10/26 20:50
 */
@Data
public class RegisterUser {
    @ApiModelProperty(value = "用户名", required = true, dataType = "String")
    private String username;

    @ApiModelProperty(value = "密码", required = true, dataType = "String")
    private String password;

    @ApiModelProperty(value = "角色名称 老师/助教 学生", required = true, dataType = "String")
    private String roleName;

    @ApiModelProperty(value = "真实姓名", required = true, dataType = "String")
    private String realName;

    @ApiModelProperty(value = "学校", required = true, dataType = "String")
    private String school;

    @ApiModelProperty(value = "邮箱", required = true, dataType = "String")
    private String email;

    @ApiModelProperty(value = "验证码", required = true, dataType = "String")
    private String code;

}
