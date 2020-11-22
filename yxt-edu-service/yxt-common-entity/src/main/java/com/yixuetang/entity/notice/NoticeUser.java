package com.yixuetang.entity.notice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程公告-用户实体类
 * @date 2020/11/11 16:32
 */
@Data
@TableName("t_notice_user")
public class NoticeUser {

    @TableId(type = IdType.AUTO)
    private Long id;    // 主键自增id

    @TableField("notice_id")
    private Long noticeId;  // 课程公告id

    @TableField("user_id")
    private Long userId;    // 用户id

    private Boolean view;   // 是否已读 true已读 false未读

}
