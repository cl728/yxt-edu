package com.yixuetang.entity.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description 存储用户消息提醒设置实体类
 * @date 2020/11/23 10:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_message_setting")
public class UserMessageSetting {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long id;        // 主键自增id

    @TableField("user_id")
    @ApiModelProperty(hidden = true)
    private Long userId;    // 用户id

    @TableField("all_remind")
    @ApiModelProperty(value = "是否接收消息提醒", required = true, dataType = "boolean")
    private Boolean allRemind;    // 是否接收消息提醒，true 是 false 不是，默认为 true

    @TableField("course")
    @ApiModelProperty(value = "是否接收课程相关消息提醒", required = true, dataType = "boolean")
    private Boolean course;    // 是否接收课程相关消息提醒，true 是 false 不是，默认为 true

    @ApiModelProperty(value = "是否接收回复我的消息提醒", required = true, dataType = "boolean")
    private Boolean reply;  // 是否接收回复我的消息提醒，true 是 false 不是，默认为 true

    @TableField("vote_up")
    @ApiModelProperty(value = "是否接收收到的赞消息提醒", required = true, dataType = "boolean")
    private Boolean voteUp; // 是否接收收到的赞消息提醒，true 是 false 不是，默认为 true

    @ApiModelProperty(value = "是否接收我的私信消息提醒", required = true, dataType = "boolean")
    private Boolean chat;   // 是否接收我的私信消息提醒，true 是 false 不是，默认为 true

}
