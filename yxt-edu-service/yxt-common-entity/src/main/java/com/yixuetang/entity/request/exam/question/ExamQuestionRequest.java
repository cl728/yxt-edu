package com.yixuetang.entity.request.exam.question;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 试题实体类
 * @date 2020/12/9 10:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionRequest {

    @ApiModelProperty(value = "题目编号", required = true, dataType = "int")
    private Integer questionNumber;

    @ApiModelProperty(value = "题目类型，0选择题，1判断题，2填空题，3问答题", required = true, dataType = "int")
    private Integer questionType;

    @ApiModelProperty(value = "题目类型名称，可选项：单选题、多选题、判断题、填空题、问答题", required = true, dataType = "int")
    private String questionTypeName;

    @ApiModelProperty(value = "题目分值", required = true, dataType = "Double")
    private Double score;

    @ApiModelProperty(value = "题干", required = true, dataType = "String")
    private String content;

    @ApiModelProperty(value = "单选题题目答案", dataType = "String")
    private String singleSelectAnswer;

    @ApiModelProperty(value = "多选题题目答案", dataType = "list")
    private List<String> multiSelectAnswer;

    @ApiModelProperty(value = "判断题题目答案", dataType = "boolean")
    private Boolean judgeAnswer;

    @ApiModelProperty(value = "填空题题目参考答案", dataType = "String")
    private String fillAnswer;

    @ApiModelProperty(value = "问答题题目参考答案", dataType = "String")
    private String questionAndAnswer;

    @ApiModelProperty(value = "题目解析", dataType = "String")
    private String analysis;

    @ApiModelProperty(value = "选择题选项列表", dataType = "list")
    private List<SelectChoice> selectChoices;

}
