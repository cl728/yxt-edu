package com.yixuetang.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.exam.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

}
