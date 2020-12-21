package com.yixuetang.entity.response.result.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/6 9:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentVoteUpCountResp {

    private Long id;

    private Integer count;

}
