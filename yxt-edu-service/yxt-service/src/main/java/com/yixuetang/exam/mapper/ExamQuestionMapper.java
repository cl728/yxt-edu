package com.yixuetang.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.exam.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/5 16:23
 */
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {

    @Select("select question_id from t_exam_question where exam_id = #{examId}")
    List<Long> findExamQuestionIds(long examId);

    @Update("update t_exam_question set question_number = question_number - 1 " +
            " where exam_id = #{examId} and question_number > #{questionNumber}")
    void updateQuestionNumberLargerThanDeleted(@Param("examId") long examId,
                                               @Param("questionNumber") long questionNumber);
}
