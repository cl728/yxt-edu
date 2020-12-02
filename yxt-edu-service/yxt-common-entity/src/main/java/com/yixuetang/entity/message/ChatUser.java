package com.yixuetang.entity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/1 20:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {

    private Long id;

    private String name;

    private String avatar;

    public void setName(String name) {
        this.name = name.trim();
    }

}
