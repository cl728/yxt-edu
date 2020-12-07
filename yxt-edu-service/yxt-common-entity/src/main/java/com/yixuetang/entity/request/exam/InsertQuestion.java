package com.yixuetang.entity.request.exam;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yang
 * @version 1.0
 * @date 2020/12/7 20:33
 * @description 保存题目的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertQuestion {

    @ApiModelProperty(value = "题目编号", required = true, dataType = "int")
    private int questionNumber;

    @ApiModelProperty(value = "题目类型", required = true, dataType = "int")
    private int questionType;

    @ApiModelProperty(value = "选择题类型", required = false, dataType = "int")
    private int selectType;

    @ApiModelProperty(value = "题目内容", required = true, dataType = "String")
    private String content;

    @ApiModelProperty(value = "选择题答案 A", required = false, dataType = "String")
    private String choiceA;

    @ApiModelProperty(value = "选择题答案 B", required = false, dataType = "String")
    private String choiceB;

    @ApiModelProperty(value = "选择题答案 C", required = false, dataType = "String")
    private String choiceC;

    @ApiModelProperty(value = "选择题答案 D", required = false, dataType = "String")
    private String choiceD;

    @ApiModelProperty(value = "参考答案", required = true, dataType = "String")
    private String answer;

    @ApiModelProperty(value = "试题解析", required = false, dataType = "String")
    private String analysis;

    @ApiModelProperty(value = "分值", required = true, dataType = "Double")
    private Double score;
}
