package com.yixuetang.entity.response.result.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2021/1/2 23:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTypeCount {

    private Integer value;

    private String name;

}
