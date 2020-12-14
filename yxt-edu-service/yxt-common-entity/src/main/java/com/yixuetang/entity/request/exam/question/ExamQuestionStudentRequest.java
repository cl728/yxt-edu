package com.yixuetang.entity.request.exam.question;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/13 22:37
 */
@Data
public class ExamQuestionStudentRequest {

    @ApiModelProperty(value = "题号", required = true, dataType = "int")
    private int questionNumber;

    @ApiModelProperty(value = "题目类型 单选题、多选题、判断题、填空题、问答题", required = true, dataType = "String")
    private String questionTypeName;

    @ApiModelProperty(value = "学生提交的单选题答案", dataType = "String")
    private String studentSingleSelectAnswer;

    @ApiModelProperty(value = "学生提交的多选题答案", dataType = "list")
    private List<String> studentMultiSelectAnswer;

    @ApiModelProperty(value = "学生提交的判断题答案", dataType = "boolean")
    private Boolean studentJudgeAnswer;

    @ApiModelProperty(value = "学生提交的填空题答案", dataType = "String")
    private String studentFillAnswer;

    @ApiModelProperty(value = "学生提交的问答题答案", dataType = "String")
    private String studentQuestionAndAnswer;

}
