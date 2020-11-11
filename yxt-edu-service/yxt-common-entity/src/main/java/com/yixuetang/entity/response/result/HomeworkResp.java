package com.yixuetang.entity.response.result;

import com.yixuetang.entity.homework.Homework;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/11 17:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HomeworkResp extends Homework {

    private Integer uncommittedCount;
    private Integer uncorrectedCount;
    private Integer correctedCount;
    private Integer status;
}
