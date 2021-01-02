package com.yixuetang.entity.response.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description 网站数据统计实体类
 * @date 2021/1/2 19:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebDataCount {

    private Integer totalVisitCount;

    private Integer dateVisitCount;

    private Integer totalDownloadCount;

    private Integer dateDownloadCount;

    private Integer totalRegisterCount;

    private Integer dateRegisterCount;

}
