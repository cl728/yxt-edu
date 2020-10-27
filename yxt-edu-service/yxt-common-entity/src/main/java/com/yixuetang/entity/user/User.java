package com.yixuetang.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户实体类
 * @date 2020/10/23 14:31
 */
@Data
@Builder
@TableName(value = "t_user")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(value = "用户名", required = true, dataType = "String")
    private String username;

    @JsonIgnore
    @ApiModelProperty(value = "密码", required = true, dataType = "String")
    private String password;

    @TableField("real_name")
    @ApiModelProperty(value = "真实姓名", required = true, dataType = "String")
    private String realName;

    @ApiModelProperty(value = "性别 男或女", dataType = "char")
    private String gender;

    @ApiModelProperty(value = "年龄", dataType = "int")
    private Integer age;

    @ApiModelProperty(value = "邮箱", required = true, dataType = "String")
    private String email;

    @ApiModelProperty(value = "头像", dataType = "String")
    private String avatar;

    @ApiModelProperty(value = "手机号码", dataType = "String")
    private String phone;

    @ApiModelProperty(value = "学校", required = true)
    private String school;

    @TableField("ts_no")
    @ApiModelProperty(value = "学号或者教工号", dataType = "String")
    private String tsNo;

    @TableField("create_time")
    @ApiModelProperty(value = "注册时间", required = true, dataType = "date")
    private Date createTime;

    @TableField("update_time")
    @ApiModelProperty(value = "最后一次更新个人信息时间", dataType = "date")
    private Date updateTime;

    @TableField(exist = false)
    private Role role;
}
