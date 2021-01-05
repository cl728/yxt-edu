package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2021/1/3 15:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {

    @ApiModelProperty(name = "用户名", dataType = "String")
    private String username;

    @ApiModelProperty(name = "密码", dataType = "String")
    private String password;

    @ApiModelProperty(name = "邮箱", dataType = "String")
    private String email;

    @ApiModelProperty(name = "手机号码", dataType = "String")
    private String phone;

    @ApiModelProperty(name = "学校", dataType = "String")
    private String school;

    @ApiModelProperty(name = "学工号", dataType = "String")
    private String teSno;

    @ApiModelProperty(name = "性别", dataType = "String")
    private String gender;

    @ApiModelProperty(name = "角色id", dataType = "long")
    private Long roleId;

}
