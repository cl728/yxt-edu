package com.yixuetang.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * @author Colin
 * @version 1.0.0
 * @description 作业-学生实体类
 * @date 2020/11/23 14:02
 */
@Data
@TableName("t_homework_resource")
public class HomeworkResource {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键自增id

    @TableField("homework_id")
    private Long homeworkId;  // 课程id

    @TableField("student_id")
    private Long studentId;

    @TableField("resource_id")
    private Long resourceId;

}
