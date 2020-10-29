package com.yixuetang.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 学校实体类
 * @date 2020/10/25 15:11
 */
@Data
@TableName("t_school")
public class School {

    @TableId(type = IdType.AUTO)
    private int id; // 学校主键自增id

    private String value; // 学校名称
}
