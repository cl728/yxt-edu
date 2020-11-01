package com.yixuetang.entity.request.user;

import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 换绑手机号码用户实体类
 * @date 2020/11/1 15:59
 */
@Data
public class PhoneUser {

    private String phone;
    private String code;

}
