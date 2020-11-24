package com.yixuetang.entity.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.user.User;
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

    @TableField("receiver_type")
    private Integer receiverType;   // 接收通知的用户类型，0全体用户 1单一用户，默认为0

    @TableField("receiver_id")
    private Long receiverId;        // 接收通知的用户id，如果发送给单一用户则为那个用户的id，否则为0，默认为0

    private Boolean status;         // 是否已经被拉取，true 是 false 否，默认为 false

    private String title;           // 通知的标题

    private String content;         // 通知的内容

    @TableField("publish_time")
    private Date publishTime;       // 发布时间

    @TableField("update_time")
    private Date updateTime;        // 最近一次更新时间

    @TableField(exist = false)
    private User publisher;         // 发布者 - 通知  一对多

    @TableField(exist = false)
    private User receiver;          // 接收者

}
