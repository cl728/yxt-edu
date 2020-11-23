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
 * @description 通知实体类
 * @date 2020/11/22 21:26
 */
@Data
@TableName("t_message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;                // 主键自增id

    @TableField("message_type")
    private Integer messageType;    // 通知类型，0系统通知 1课程通知

    @TableField("receiver_type")
    private Integer receiverType;   // 接收通知的用户类型，0全体用户 1关联的课程下所有学生用户 2单一用户，默认为0

    @TableField("publisher_id")
    private Long publisherId;       // 发布者id

    @TableField("receiver_id")
    private Long receiverId;        // 接收通知的用户id，如果发送给单一用户则为那个用户的id，否则为0，默认为0

    @TableField("course_id")
    private Long courseId;          // 可能关联的课程id，如果通知类型为系统通知，则为 null

    private Boolean status;         // 是否已经被拉取，true 是 false 否，默认为 false

    private String title;           // 通知的标题

    private String content;         // 通知的内容

    @TableField("publish_time")
    private Date publishTime;       // 发布时间

}
