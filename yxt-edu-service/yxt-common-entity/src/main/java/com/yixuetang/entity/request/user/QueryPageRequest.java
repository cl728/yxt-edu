package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 后台管理页面分页查询实体类
 * @date 2020/10/30 20:57
 */
@Data
public class QueryPageRequest {

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户姓名")
    private String realName;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty(hidden = true)
    private Long roleId;

}
