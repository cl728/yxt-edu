package com.yixuetang.entity.comment;

import lombok.Getter;

/**
 * @author Colin
 * @version 1.0.0
 * @description 点赞状态枚举类
 * @date 2020/12/5 18:51
 */
@Getter
public enum LikedStatusEnum {

    LIKE( 1, "点赞" ),
    UNLIKE( 0, "取消点赞/未点赞" ),
    ;

    private Integer code;

    private String msg;

    LikedStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
