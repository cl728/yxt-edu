package com.yixuetang.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户实体类
 * @date 2020/10/23 14:31
 */
@Data
@TableName(value = "t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    @TableField("real_name")
    private String realName;
    private String gender;
    private Integer age;
    private String email;
    private String avatar;
    private String phone;
    private String school;
    @TableField("ts_no")
    private String tsNo;

    @TableField(exist = false)
    private Role role;
}
