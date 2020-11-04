package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 删除课程的用户实体类
 * @date 2020/11/4 14:59
 */
@Data
public class DelCourseUser {

    @ApiModelProperty(value = "用户id", required = true, dataType = "String")
    private Long userId;

    @ApiModelProperty(value = "用户密码", required = true, dataType = "String")
    private String password;

}
