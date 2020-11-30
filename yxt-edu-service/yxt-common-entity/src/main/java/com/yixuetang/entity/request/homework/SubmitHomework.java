package com.yixuetang.entity.request.homework;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 提交作业请求实体类
 * @date 2020/11/30 0:08
 */
@Data
public class SubmitHomework {

    @ApiModelProperty(value = "上传的作业列表id", required = true, dataType = "List")
    private List<Long> resourceIds;

}
