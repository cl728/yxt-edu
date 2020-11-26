package com.yixuetang.entity.response.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description 封装用户未读的系统通知或事件提醒数量
 * @date 2020/11/25 13:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCountResp {

    private Integer systemMessageCount;

    private Integer courseMessageCount;

    private Integer replyMessageCount;

    private Integer voteUpMessageCount;

    private Integer chatMessageCount;

}
