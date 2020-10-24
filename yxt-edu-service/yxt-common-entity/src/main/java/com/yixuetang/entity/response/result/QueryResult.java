package com.yixuetang.entity.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 通用的查询结果集
 * @date 2020/10/23 15:55
 */
@Data
@AllArgsConstructor
public class QueryResult<T> {

    // 数据列表
    private List<T> data;

    // 数据总数
    private int total;

}
