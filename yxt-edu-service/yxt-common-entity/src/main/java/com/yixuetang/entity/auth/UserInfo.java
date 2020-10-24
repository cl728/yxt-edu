package com.yixuetang.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description JWT鉴权载荷对象
 * @date 2020/10/23 19:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Long id;
    private String username;
    private String realName;
    private String avatar;

    public UserInfo(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
