package com.yixuetang.entity.response.result.user;

import com.yixuetang.entity.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/12 9:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserResp extends User {

    private Date joinTime;
    private String clazz;

}
