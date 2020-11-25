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
 * @description 存储用户接收的通知（包括系统通知和课程通知）实体类
 * @date 2020/11/22 17:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_message")
public class UserMessage {

    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键自增id

    @TableField("message_id")
    private Long messageId;             // 通知id

    @TableField("receiver_id")
    private Long receiverId;            // 接收通知的用户id

    private Boolean status;             // 是否已读，true 是 false 不是，默认为 false

    @TableField("pull_time")
    private Date pullTime;              // 拉取通知时间

}
