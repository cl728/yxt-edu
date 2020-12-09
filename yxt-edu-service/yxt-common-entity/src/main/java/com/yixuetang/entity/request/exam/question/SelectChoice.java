package com.yixuetang.entity.request.exam.question;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Colin
 * @version 1.0.0
 * @description 选择题选项实体类
 * @date 2020/12/9 10:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectChoice {

    @ApiModelProperty(value = "选项名称", dataType = "String")
    private String name;

    @ApiModelProperty(value = "选项答案", dataType = "String")
    private String value;

}
