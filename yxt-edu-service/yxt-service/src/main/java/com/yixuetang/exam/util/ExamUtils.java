package com.yixuetang.exam.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.entity.exam.ExamQuestion;
import com.yixuetang.entity.exam.ExamQuestionStudent;
import com.yixuetang.exam.mapper.ExamQuestionMapper;
import com.yixuetang.exam.mapper.ExamQuestionStudentMapper;
import com.yixuetang.exam.mapper.question.FillMapper;
import com.yixuetang.exam.mapper.question.JudgeMapper;
import com.yixuetang.exam.mapper.question.QAMapper;
import com.yixuetang.exam.mapper.question.SelectMapper;
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

    @Autowired
    private SelectMapper selectMapper;

    @Autowired
    private JudgeMapper judgeMapper;

    @Autowired
    private FillMapper fillMapper;

    @Autowired
    private QAMapper qaMapper;

    /**
     * 获取某个学生在某次测试中的得分
     *
     * @param examId    测试id
     * @param studentId 学生id
     * @return 学生在某次测试中的得分
     */
    public double getTotalScore(long examId, long studentId) {
        Set<Long> examQuestionIdSet = getExamQuestionIdSet( examId );

        List<ExamQuestionStudent> examQuestionStudents = getExamQuestionStudentList( studentId, examQuestionIdSet );

        double totalScore = 0.0;
        for (ExamQuestionStudent examQuestionStudent : examQuestionStudents) {
            Double examQuestionStudentScore = examQuestionStudent.getScore();
            totalScore += examQuestionStudentScore == null ? 0.0 : examQuestionStudentScore;
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

    /**
     * 获取某次测试的总分（满分）
     *
     * @param examId 测试id
     * @return 测试的总分（满分）
     */
    public Double getExamTotalScore(Long examId) {
        double examTotalScore = 0.0;

        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(
                new QueryWrapper<ExamQuestion>()
                        .eq( "exam_id", examId ) );

        for (ExamQuestion examQuestion : examQuestionList) {
            examTotalScore += this.getQuestionScore( examQuestion.getQuestionType(), examQuestion.getQuestionId() );
        }

        return examTotalScore;
    }

    public Double getQuestionScore(Integer questionType, Long questionId) {
        switch (questionType) {
            case 0:
                return this.selectMapper.selectById( questionId ).getScore();
            case 1:
                return this.judgeMapper.selectById( questionId ).getScore();
            case 2:
                return this.fillMapper.selectById( questionId ).getScore();
            case 3:
                return this.qaMapper.selectById( questionId ).getScore();
            default:
                return null;
        }
    }
}
