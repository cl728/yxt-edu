package com.yixuetang.entity.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 修改用户信息实体类
 * @date 2020/10/27 19:56
 */
@Data
public class UpdateUser {

    @ApiModelProperty(value = "真实姓名", required = true, dataType = "String")
    private String realName;

    @ApiModelProperty(value = "性别", required = true, dataType = "String")
    private String gender;

    @ApiModelProperty(value = "年龄", required = true, dataType = "int")
    private int age;

    @ApiModelProperty(value = "手机号码", required = true, dataType = "String")
    private String phone;

    @ApiModelProperty(value = "学校", required = true, dataType = "String")
    private String school;

    @ApiModelProperty(value = "角色名称 老师/助教 学生", required = true, dataType = "String")
    private String roleName;

    @ApiModelProperty(value = "学/工号", required = true, dataType = "String")
    private String tsNo;

    @ApiModelProperty(value = "最后一次更新个人信息时间", dataType = "date")
    private Date updateTime;

}
