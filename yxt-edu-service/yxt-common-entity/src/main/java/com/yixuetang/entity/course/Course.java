package com.yixuetang.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程模块课程实体类
 * @date 2020/10/27 21:24
 */
@Data
@TableName("t_course")
public class Course {

    @TableId(type = IdType.AUTO)
    private long id;

    @TableField("teacher_id")
    private long teacherId;

    @TableField("c_name")
    private String cName;

    @TableField("c_pic")
    private String cPic;

    @TableField("c_code")
    private String cCode;

    @TableField("s_count")
    private String sCount;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("school_year")
    private String schoolYear;

    private String semester;

}
