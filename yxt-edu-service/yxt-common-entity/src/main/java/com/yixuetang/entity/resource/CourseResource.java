package com.yixuetang.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.course.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程-资源实体类
 * @date 2020/11/17 16:10
 */
@TableName("t_cr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("course_id")
    private Long courseId;

    @TableField("resource_id")
    private Long resourceId;

    @TableField(exist = false)
    private Course course;

    @TableField(exist = false)
    private Resource resource;

}
