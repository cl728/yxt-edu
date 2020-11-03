package com.yixuetang.entity.request.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 转让课程的实体类
 * @date 2020/11/03 09:40
 */
@Data
public class TransferCourse {

    @ApiModelProperty(value = "请求转让教师密码", required = true, dataType = "String")
    private String password;

    @ApiModelProperty(value = "接受转让教师邮箱", required = true, dataType = "String")
    private String email;
}
