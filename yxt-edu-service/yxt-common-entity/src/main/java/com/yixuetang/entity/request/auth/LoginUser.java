package com.yixuetang.entity.request.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户登录实体类
 * @date 2020/10/24 10:42
 */
@Data
public class LoginUser {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("邮箱地址")
    private String email;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("下次是否自动登录 true是 false不是")
    private boolean rememberMe;

}
