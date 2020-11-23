package com.yixuetang.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 作业-学生-资源实体类
 * @date 2020/11/22 13:20
 */
@Data
@TableName("t_homework_resource")
public class HomeworkStudentResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("homework_id")
    private Long homeworkId;

    @TableField("student_id")
    private Long studentId;

    private Long resourceId;

}
