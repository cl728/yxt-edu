package com.yixuetang.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 角色实体类
 * @date 2020/10/23 14:31
 */
@Data
@TableName("t_role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id; // 角色主键自增id

    @TableField("r_name")
    private String rName; // 角色名称

}
