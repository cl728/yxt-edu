package com.yixuetang.entity.response.result;

import com.yixuetang.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Colin
 * @version 1.0.0
 * @description 事件提醒响应实体类
 * @date 2020/11/25 19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRemindResp {

    private Long id;

    private Integer remindType;

    private User sender;

    private String action;

    private String sourceName;

    private String sourceContent;

    private String targetContent;

    private String url;

    private Date remindTime;

}
