package com.yixuetang.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yixuetang.entity.homework.HomeworkStudent;
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
    private Long id; //用户主键自增id

    private String username; //用户名

    @JsonIgnore
    private String password; //密码

    @TableField("real_name")
    private String realName; // 真实姓名

    private String gender; // 性别

    private Integer age; // 年龄

    private String email; // 电子邮箱地址

    private String avatar; // 头像

    private String phone; // 联系电话

    private String school; // 学校

    @TableField("ts_no")
    private String tsNo; // 学/工号

    @TableField("create_time")
    private Date createTime; // 注册时间

    @TableField("update_time")
    private Date updateTime; // 最后一次更新个人信息时间

    @TableField(exist = false)
    private Role role; // 角色

    @TableField(exist = false)
    private HomeworkStudent homeworkStudent;

    @TableField(exist = false)
    private String courseName;  // 这是为了展示私信聊天页面用户所在课程名称而增设的字段

}
