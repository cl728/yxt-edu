package com.yixuetang.entity.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.notice.Notice;
import com.yixuetang.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 评论实体类
 * @date 2020/11/20 20:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;    // 主键自增id

    private String content; // 评论内容

    @TableField("create_time")
    private Date createTime;    // 发布时间

    @TableField(exist = false)
    private Notice notice;  // 评论-公告 一对多

    @TableField(exist = false)
    private User user;  // 评论-用户 一对多

    @TableField(exist = false)
    private Comment parentComment;  // 评论-父级评论 一对多

    @TableField(exist = false)
    private List<Comment> childComments;    // 评论-子级评论 多对一

    @TableField(exist = false)
    private Integer voteUpCount;    // 被点赞次数
}
