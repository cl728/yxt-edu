package com.yixuetang.exam.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.entity.exam.ExamQuestion;
import com.yixuetang.entity.exam.ExamQuestionStudent;
import com.yixuetang.exam.mapper.ExamQuestionMapper;
import com.yixuetang.exam.mapper.ExamQuestionStudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/17 17:23
 */
@Component
public class ExamUtils {

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private ExamQuestionStudentMapper examQuestionStudentMapper;

    public double getTotalScore(long examId, long studentId) {
        Set<Long> examQuestionIdSet = getExamQuestionIdSet( examId );

        List<ExamQuestionStudent> examQuestionStudents = getExamQuestionStudentList( studentId, examQuestionIdSet );

        double totalScore = 0.0;
        for (ExamQuestionStudent examQuestionStudent : examQuestionStudents) {
            totalScore += examQuestionStudent.getScore();
        }

        return totalScore;
    }

    public Set<Long> getExamQuestionIdSet(long examId) {
        return examQuestionMapper.selectList(
                new QueryWrapper<ExamQuestion>()
                        .eq( "exam_id", examId ) )
                .stream()
                .map( ExamQuestion::getId )
                .collect( Collectors.toSet() );
    }

    public List<ExamQuestionStudent> getExamQuestionStudentList(long studentId, Set<Long> examQuestionIdSet) {
        return examQuestionStudentMapper.selectList(
                new QueryWrapper<ExamQuestionStudent>()
                        .eq( "student_id", studentId ) )
                .stream()
                .filter( examQuestionStudent ->
                        examQuestionIdSet.contains( examQuestionStudent.getExamQuestionId() )
                                && examQuestionStudent.getScore() != null )
                .collect( Collectors.toList() );
    }
}
