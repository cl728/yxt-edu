package com.yixuetang.entity.notice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.course.Course;
import lombok.Data;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程公告实体类
 * @date 2020/11/11 16:24
 */
@Data
@TableName("t_notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;    // 主键自增id

    private String title;   // 公告标题

    private String content; // 公告内容

    @TableField("create_time")
    private Date createTime; // 发布时间

    @TableField("update_time")
    private Date updateTime; // 最后一次更新时间

    @TableField(exist = false)
    private Course course;  // 课程-公告 一对多

    @TableField(exist = false)
    private Long views;  //已读人数
}
