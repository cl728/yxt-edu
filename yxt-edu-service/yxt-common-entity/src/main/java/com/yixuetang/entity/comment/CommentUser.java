package com.yixuetang.entity.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 评论点赞-用户记录表
 * @date 2020/11/29 12:44
 */
@Data
@TableName("t_comment_user")
public class CommentUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("comment_id")
    private Long commentId;

    @TableField("user_id")
    private Long userId;

    @TableField("status")
    private Boolean status;

}
