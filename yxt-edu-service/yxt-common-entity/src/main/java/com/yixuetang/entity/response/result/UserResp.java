package com.yixuetang.entity.response.result;

import com.yixuetang.entity.user.User;
import lombok.Data;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/12 9:30
 */
@Data
public class UserResp extends User {

    private Date joinTime;
    private String clazz;

}
