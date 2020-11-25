package com.yixuetang.entity.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 事件提醒实体类
 * @date 2020/11/25 12:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_remind")
public class EventRemind {

    @TableId(type = IdType.AUTO)
    private Long id;                // 主键自增id

    @TableField("remind_type")
    private Integer remindType;     // 事件提醒类型，0课程相关，1回复，2点赞，3私信

    @TableField("sender_id")
    private Long senderId;          // 操作者id，即由谁引起的事件提醒

    @TableField("course_id")
    private Long courseId;          // 事件可能关联的课程id

    @TableField("receiver_id")
    private Long receiverId;        // 接收者id

    @TableField("source_id")
    private Long sourceId;          // 事件源id，如评论id，作业id等

    @TableField("target_id")
    private Long targetId;          // 目标源id，如被回复、被点赞的评论id等

    private String action;          // 动作类型，如回复、点赞、发布等

    @TableField("source_name")
    private String sourceName;      // 事件源名称，如作业、公告等

    private String url;             // 事件所发生的地点链接

    private Boolean status;         // 是否已读

    @TableField("remind_time")
    private Date remindTime;        // 提醒时间

}
