package com.yixuetang.entity.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 事件提醒实体类
 * @date 2020/11/25 12:48
 */
@Data
@TableName("t_remind")
public class EventRemind {

    @TableId(type = IdType.AUTO)
    private Long id;            // 主键自增id

    @TableField("remind_type")
    private Integer remindType; // 事件提醒类型，0课程相关，1回复，2点赞，3私信

    @TableField("sender_id")
    private Long senderId;      // 操作者id，即由谁引起的事件提醒

    @TableField("receiver_id")
    private Long receiverId;    // 接收者id

    @TableField("source_id")
    private Long sourceId;      // 事件源id，如评论id，作业id等

    @TableField("source_type")
    private String sourceType;  // 事件源类型，如回复、点赞、发布等

    private String url;         // 事件所发生的地点链接

    private Boolean status;     // 是否已读

    @TableField("remind_time")
    private Date remindTime;    // 提醒时间

}
