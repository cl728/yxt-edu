package com.yixuetang.entity.request.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 发布公告评论的实体类
 * @date 2020/11/22 20:46
 */
@Data
public class PostComment {

    @ApiModelProperty(value = "评论内容", required = true, dataType = "String")
    private String content;

    @ApiModelProperty(value = "父级评论id", required = false, dataType = "long")
    private long parentCommentId;
}
