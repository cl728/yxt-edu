package com.yixuetang.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.exam.Exam;
import com.yixuetang.entity.exam.ExamQuestion;
import com.yixuetang.entity.exam.question.Fill;
import com.yixuetang.entity.exam.question.Judge;
import com.yixuetang.entity.exam.question.QA;
import com.yixuetang.entity.exam.question.Select;
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.InsertQuestion;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.exam.ExamCode;
import com.yixuetang.entity.response.code.homework.HomeworkCode;
import com.yixuetang.entity.user.User;
import com.yixuetang.exam.mapper.ExamMapper;
import com.yixuetang.exam.mapper.ExamQuestionMapper;
import com.yixuetang.exam.mapper.question.FillMapper;
import com.yixuetang.exam.mapper.question.JudgeMapper;
import com.yixuetang.exam.mapper.question.QAMapper;
import com.yixuetang.exam.mapper.question.SelectMapper;
import com.yixuetang.exam.service.ExamService;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Curits
 * @version 1.0.0
 * @date 2020/11/07 10:26
 */
@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SelectMapper selectMapper;

    @Autowired
    private JudgeMapper judgeMapper;

    @Autowired
    private QAMapper qaMapper;

    @Autowired
    private FillMapper fillMapper;

    @Autowired
    ExamQuestionMapper examQuestionMapper;

    @Override
    public CommonResponse newExam(long courseId, InsertExam insertExam) {
        // 1. courseId 不合法
        Course course = this.courseMapper.findById( courseId );
        if (course == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 参数验证
        if (insertExam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 3. 测试题目非空判断
        if (!StringUtils.isNoneBlank( insertExam.getTitle() )) {
            return new CommonResponse(ExamCode.INSERT_EXAM_FAIL_TITLE_IS_NULL);
        }

        // 4. 测试简介非空判断
        if (!StringUtils.isNoneBlank( insertExam.getIntroduction() )) {
            return new CommonResponse( ExamCode.INSERT_EXAM_FAIL_INTRODUCTION_IS_NULL );
        }

        // 5.根据课程id新建测试
        Exam exam = new Exam();
        exam.setCourseId( courseId );
        exam.setTitle( insertExam.getTitle() );
        exam.setIntroduction( insertExam.getIntroduction() );
        exam.setStartTime( insertExam.getStartTime() );
        exam.setEndTime( insertExam.getEndTime() );
        exam.setCreateTime( new Date() );
        examMapper.insert( exam );

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse examTop(long examId) {
        //测试考试是否存在
        Exam exam = examMapper.selectOne(new QueryWrapper<Exam>().eq("id", examId));
        if(exam == null){
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        //置顶或取消置顶
        if(exam.getTopNum() <= 0){
            exam.setTopNum(10);
        } else{
            exam.setTopNum(0);
        }
        examMapper.updateById(exam);
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse saveQuestion(long examId, long teacherId, InsertQuestion insertQuestion) {
        //测试考试是否存在
        Exam exam = examMapper.selectOne(new QueryWrapper<Exam>().eq("id", examId));
        if(exam == null){
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        //判断教师是否为该课程下的老师
        Course course = courseMapper.selectOne(new QueryWrapper<Course>().eq("id", exam.getCourseId()).eq("teacher_id", teacherId));
        if(course == null){
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        //根据 question_type 将题目保存到相应的表里
        //插入数据后返回主键id
        Long questionId = null;
        if(insertQuestion.getQuestionType() == 0){
            Select select = new Select();
            select.setTeacherId(teacherId);
            select.setType(insertQuestion.getSelectType());
            select.setContent(insertQuestion.getContent());
            select.setChoiceA(insertQuestion.getChoiceA());
            select.setChoiceB(insertQuestion.getChoiceB());
            select.setChoiceC(insertQuestion.getChoiceC());
            select.setChoiceD(insertQuestion.getChoiceD());
            select.setAnswer(insertQuestion.getAnswer());
            select.setAnalysis(insertQuestion.getAnalysis());
            select.setScore(insertQuestion.getScore());
            select.setCreateTime(new Date());
            selectMapper.insert(select);
            questionId = select.getId();
        }else if(insertQuestion.getQuestionType() == 1){
            Judge judge = new Judge();
            judge.setTeacherId(teacherId);
            judge.setContent(insertQuestion.getContent());
            judge.setAnswer("true".equals(insertQuestion.getAnswer())?true:false);
            judge.setAnalysis(insertQuestion.getAnalysis());
            judge.setScore(insertQuestion.getScore());
            judge.setCreateTime(new Date());
            judgeMapper.insert(judge);
            questionId = judge.getId();
        }else if(insertQuestion.getQuestionType() == 2){
            Fill fill = new Fill();
            fill.setTeacherId(teacherId);
            fill.setContent(insertQuestion.getContent());
            fill.setAnswer(insertQuestion.getAnswer());
            fill.setAnalysis(insertQuestion.getAnalysis());
            fill.setScore(insertQuestion.getScore());
            fill.setCreateTime(new Date());
            fillMapper.insert(fill);
            questionId = fill.getId();
        }else if(insertQuestion.getQuestionType() == 3){
            QA qa = new QA();
            qa.setTeacherId(teacherId);
            qa.setContent(insertQuestion.getContent());
            qa.setAnswer(insertQuestion.getAnswer());
            qa.setAnalysis(insertQuestion.getAnalysis());
            qa.setScore(insertQuestion.getScore());
            qa.setCreateTime(new Date());
            qaMapper.insert(qa);
            questionId = qa.getId();
        }else {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        //保存相关记录到 t_exam_question 表里
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExamId(examId);
        examQuestion.setQuestionNumber(insertQuestion.getQuestionNumber());
        examQuestion.setQuestionType(insertQuestion.getQuestionType());
        examQuestion.setQuestionId(questionId);
        examQuestionMapper.insert(examQuestion);

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse deleteQuestion(long examId, long questionNumber) {
        //测试考试是否存在
        Exam exam = examMapper.selectOne(new QueryWrapper<Exam>().eq("id", examId));
        if(exam == null){
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        //题目是否存在
        ExamQuestion examQuestion = examQuestionMapper.selectOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examId)
                .eq("question_number", questionNumber));
        if(examQuestion == null){
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        //根据 examId, questionNumber 删除 t_exam_quesiton 的相关记录
        examQuestionMapper.delete(new QueryWrapper<ExamQuestion>().eq("exam_id", examId)
                .eq("question_number", questionNumber));

        return new CommonResponse(CommonCode.SUCCESS);
    }
}
