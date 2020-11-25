package com.yixuetang.entity.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 通知实体类
 * @date 2020/11/22 21:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_message")
public class Message {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long id;                // 主键自增id

    @TableField("receiver_type")
    @ApiModelProperty(value = "接收通知的用户类型，0全体用户 1单一用户", required = true, dataType = "int")
    private Integer receiverType;   // 接收通知的用户类型，0全体用户 1单一用户，默认为0

    @TableField("publisher_id")
    @ApiModelProperty(hidden = true)
    private Long publisherId;       // 发布通知的管理员id

    @TableField("receiver_id")
    @ApiModelProperty(value = "接收通知的用户id，如果发送给单一用户则为那个用户的id，否则为0", dataType = "long")
    private Long receiverId;        // 接收通知的用户id，如果发送给单一用户则为那个用户的id，否则为0，默认为0

    @ApiModelProperty(hidden = true)
    private Boolean status;         // 是否已经被拉取，true 是 false 否，默认为 false

    @ApiModelProperty(value = "通知的标题", required = true, dataType = "String")
    private String title;           // 通知的标题

    @ApiModelProperty(value = "通知的内容", required = true, dataType = "String")
    private String content;         // 通知的内容

    @TableField("publish_time")
    @ApiModelProperty(hidden = true)
    private Date publishTime;       // 发布时间

    @TableField("update_time")
    @ApiModelProperty(hidden = true)
    private Date updateTime;        // 最近一次更新时间

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private User publisher;         // 发布者 - 通知  一对多

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private User receiver;          // 接收者

}
