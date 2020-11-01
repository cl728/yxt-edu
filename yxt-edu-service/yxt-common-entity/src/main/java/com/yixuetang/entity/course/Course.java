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
    private Long id; // 课程主键自增id

    @TableField("teacher_id")
    private Long teacherId; // 授课教师主键id

    @TableField("c_name")
    private String cName; // 课程名称

    @TableField("c_pic")
    private String cPic; // 课程图片

    @TableField("c_code")
    private String cCode; // 6位课程码（由英文字母组成）

    @TableField("s_count")
    private Integer sCount; // 已加课学生人数

    @TableField("create_time")
    private Date createTime; // 开课时间

    @TableField("update_time")
    private Date updateTime; // 最后一次更新课程时间

    @TableField("school_year")
    private String schoolYear; // 学年

    private String semester; // 学期

    @TableField("class")
    private String clazz; // 班级

    @TableField("top_num")
    private Integer topNum; // 置顶字段，0代表不置顶

}
