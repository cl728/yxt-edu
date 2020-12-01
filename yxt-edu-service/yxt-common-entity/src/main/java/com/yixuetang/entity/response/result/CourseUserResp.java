package com.yixuetang.entity.response.result;

import com.yixuetang.entity.user.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程-成员响应实体类
 * @date 2020/11/30 19:28
 */
@Data
@Builder
public class CourseUserResp {

    private Long id;

    private String username;

    private List<User> userList;

}
