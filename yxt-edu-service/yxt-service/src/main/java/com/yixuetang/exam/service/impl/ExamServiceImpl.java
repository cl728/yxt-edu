package com.yixuetang.exam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.course.StudentCourse;
import com.yixuetang.entity.exam.Exam;
import com.yixuetang.entity.exam.ExamQuestion;
import com.yixuetang.entity.exam.ExamQuestionStudent;
import com.yixuetang.entity.exam.ExamStudent;
import com.yixuetang.entity.exam.question.Fill;
import com.yixuetang.entity.exam.question.Judge;
import com.yixuetang.entity.exam.question.QA;
import com.yixuetang.entity.exam.question.Select;
import com.yixuetang.entity.request.exam.EditStudentScoreRequest;
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.question.ExamQuestionRequest;
import com.yixuetang.entity.request.exam.question.ExamQuestionStudentRequest;
import com.yixuetang.entity.request.exam.question.SelectChoice;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.exam.ExamCode;
import com.yixuetang.entity.response.result.ExamResp;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.exam.mapper.ExamMapper;
import com.yixuetang.exam.mapper.ExamQuestionMapper;
import com.yixuetang.exam.mapper.ExamQuestionStudentMapper;
import com.yixuetang.exam.mapper.ExamStudentMapper;
import com.yixuetang.exam.mapper.question.FillMapper;
import com.yixuetang.exam.mapper.question.JudgeMapper;
import com.yixuetang.exam.mapper.question.QAMapper;
import com.yixuetang.exam.mapper.question.SelectMapper;
import com.yixuetang.exam.service.ExamService;
import com.yixuetang.mq.AmqpUtils;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScMapper scMapper;

    @Autowired
    private ExamStudentMapper examStudentMapper;

    @Autowired
    private ExamQuestionStudentMapper examQuestionStudentMapper;

    @Autowired
    private AmqpUtils amqpUtils;

    @Transactional
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
            ExceptionThrowUtils.cast( ExamCode.INSERT_EXAM_FAIL_TITLE_IS_NULL );
        }

        // 4. 测试简介非空判断
        if (!StringUtils.isNoneBlank( insertExam.getIntroduction() )) {
            ExceptionThrowUtils.cast( ExamCode.INSERT_EXAM_FAIL_INTRODUCTION_IS_NULL );
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

        // 插入相关记录到 t_exam_student 表中
        this.scMapper.selectList(
                new QueryWrapper<StudentCourse>()
                        .eq( "course_id", courseId ) )
                .stream()
                .map( StudentCourse::getStudentId )
                .collect( Collectors.toList() )
                .forEach( studentId ->
                        this.examStudentMapper.insert(
                                ExamStudent.builder()
                                        .id( null )
                                        .examId( exam.getId() )
                                        .studentId( studentId )
                                        .score( null )
                                        .status( 0 )
                                        .submitTime( null )
                                        .build() )
                );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    public QueryResponse findExamById(long examId) {
        // 测试考试是否存在
        Exam exam = examMapper.selectOne( new QueryWrapper<Exam>().eq( "id", examId ) );
        if (exam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 统计测试的总题数
        QueryWrapper<ExamQuestion> examQuestionQueryWrapper = new QueryWrapper<ExamQuestion>().eq( "exam_id", examId );
        exam.setQuestionCount( this.examQuestionMapper.selectCount( examQuestionQueryWrapper ) );

        // 统计测试的总分数
        double score = 0.0;
        for (ExamQuestion examQuestion : this.examQuestionMapper.selectList( examQuestionQueryWrapper )) {
            Double questionScore = getQuestionScore( examQuestion.getQuestionType(), examQuestion.getQuestionId() );
            score += ObjectUtils.isEmpty( questionScore ) ? 0.0 : questionScore;
        }
        exam.setTotalScore( score );

        // 查询关联的课程
        exam.setCourse( this.courseMapper.selectById( exam.getCourseId() ) );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( Collections.singletonList( exam ), 1 ) );
    }

    @Override
    public CommonResponse examTop(long examId) {
        //测试考试是否存在
        Exam exam = examMapper.selectOne( new QueryWrapper<Exam>().eq( "id", examId ) );
        if (exam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        //置顶或取消置顶
        if (exam.getTopNum() <= 0) {
            exam.setTopNum( 10 );
        } else {
            exam.setTopNum( 0 );
        }
        examMapper.updateById( exam );
        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Transactional
    @Override
    public CommonResponse saveQuestion(long examId, long teacherId, ExamQuestionRequest examQuestionRequest) throws JsonProcessingException {
        // 测试考试是否存在
        Exam exam = examMapper.selectOne( new QueryWrapper<Exam>().eq( "id", examId ) );
        if (exam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 判断教师是否为该课程下的老师
        Course course = courseMapper.selectOne( new QueryWrapper<Course>().eq( "id", exam.getCourseId() ).eq( "teacher_id", teacherId ) );
        if (course == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 查询是否已有相关记录
        ExamQuestion question = this.examQuestionMapper.selectOne( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId ).eq( "question_number", examQuestionRequest.getQuestionNumber() ) );
        boolean isNoneExist = ObjectUtils.isEmpty( question );

        // 根据 question_type 将题目保存到相应的表里
        // 插入数据后返回主键id
        Long questionId = null;
        if (examQuestionRequest.getQuestionType() == 0) {
            Select select = new Select();
            select.setType( StringUtils.equals( examQuestionRequest.getQuestionTypeName(), "单选题" ) ? 0 : 1 );
            select.setContent( examQuestionRequest.getContent() );
            select.setChoices( JSONObject.toJSONString( examQuestionRequest.getSelectChoices() ) );
            List<String> multiSelectAnswer = examQuestionRequest.getMultiSelectAnswer();
            if (!CollectionUtils.isEmpty( multiSelectAnswer )) {
                Collections.sort( multiSelectAnswer );
            }
            select.setAnswer( select.getType() == 0 ? examQuestionRequest.getSingleSelectAnswer() : JSONObject.toJSONString( multiSelectAnswer ) );
            select.setAnalysis( examQuestionRequest.getAnalysis() );
            select.setScore( examQuestionRequest.getScore() );
            if (isNoneExist) {
                select.setTeacherId( teacherId );
                select.setCreateTime( new Date() );
                selectMapper.insert( select );
            } else {
                selectMapper.update( select, new UpdateWrapper<Select>().eq( "id", question.getQuestionId() ) );
            }
            questionId = select.getId();
        } else if (examQuestionRequest.getQuestionType() == 1) {
            Judge judge = new Judge();
            judge.setContent( examQuestionRequest.getContent() );
            judge.setAnswer( examQuestionRequest.getJudgeAnswer() );
            judge.setAnalysis( examQuestionRequest.getAnalysis() );
            judge.setScore( examQuestionRequest.getScore() );
            if (isNoneExist) {
                judge.setTeacherId( teacherId );
                judge.setCreateTime( new Date() );
                judgeMapper.insert( judge );
            } else {
                judgeMapper.update( judge, new UpdateWrapper<Judge>().eq( "id", question.getQuestionId() ) );
            }
            questionId = judge.getId();
        } else if (examQuestionRequest.getQuestionType() == 2) {
            Fill fill = new Fill();
            fill.setContent( examQuestionRequest.getContent() );
            fill.setAnswer( examQuestionRequest.getFillAnswer() );
            fill.setAnalysis( examQuestionRequest.getAnalysis() );
            fill.setScore( examQuestionRequest.getScore() );
            if (isNoneExist) {
                fill.setTeacherId( teacherId );
                fill.setCreateTime( new Date() );
                fillMapper.insert( fill );
            } else {
                fillMapper.update( fill, new UpdateWrapper<Fill>().eq( "id", question.getQuestionId() ) );
            }
            questionId = fill.getId();
        } else if (examQuestionRequest.getQuestionType() == 3) {
            QA qa = new QA();
            qa.setContent( examQuestionRequest.getContent() );
            qa.setAnswer( examQuestionRequest.getQuestionAndAnswer() );
            qa.setAnalysis( examQuestionRequest.getAnalysis() );
            qa.setScore( examQuestionRequest.getScore() );
            if (isNoneExist) {
                qa.setTeacherId( teacherId );
                qa.setCreateTime( new Date() );
                qaMapper.insert( qa );
            } else {
                qaMapper.update( qa, new UpdateWrapper<QA>().eq( "id", question.getQuestionId() ) );
            }
            questionId = qa.getId();
        } else {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 保存相关记录到 t_exam_question 表里
        if (isNoneExist) {
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId( examId );
            examQuestion.setQuestionNumber( examQuestionRequest.getQuestionNumber() );
            examQuestion.setQuestionType( examQuestionRequest.getQuestionType() );
            examQuestion.setQuestionId( questionId );
            examQuestionMapper.insert( examQuestion );
        }

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Transactional
    @Override
    public CommonResponse deleteQuestion(long examId, long questionNumber) {
        // 测试考试是否存在
        Exam exam = examMapper.selectOne( new QueryWrapper<Exam>().eq( "id", examId ) );
        if (exam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        // 题目是否存在
        ExamQuestion examQuestion = examQuestionMapper.selectOne( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId )
                .eq( "question_number", questionNumber ) );
        if (examQuestion == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        // 根据 examId, questionNumber 删除 t_exam_question 的相关记录
        examQuestionMapper.delete( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId )
                .eq( "question_number", questionNumber ) );

        // 更新题号：比该题目题号大的题目需要减一
        examQuestionMapper.updateQuestionNumberLargerThanDeleted( examId, questionNumber );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    public QueryResponse findListByCourseId(long courseId, long userId) {

        // courseId，userId 不合法
        Course course = this.courseMapper.selectById( courseId );
        User user = this.userMapper.findById( userId );
        if (!ObjectUtils.allNotNull( course, user )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        List<Exam> examList = new ArrayList<>();

        long roleId = user.getRole().getId();

        if (roleId == 2) {  // 教师可以查询该门课程下的全部测试
            examList = this.examMapper.selectList( new QueryWrapper<Exam>()
                    .eq( "course_id", courseId )
                    .orderByDesc( "top_num", "create_time" ) );
        } else if (roleId == 3) {   // 学生只能查询已发布的测试
            examList = this.examMapper.selectList( new QueryWrapper<Exam>()
                    .eq( "course_id", courseId )
                    .eq( "status", Boolean.TRUE )
                    .orderByDesc( "top_num", "create_time" ) );
        }

        List<ExamResp> examRespList = new ArrayList<>( examList.size() );

        examList.forEach( exam -> {
            ExamResp examResp = new ExamResp();

            BeanUtils.copyProperties( exam, examResp );

            if (roleId == 2) { // 如果是教师查询，则统计未提交、待批改、已批改人数
                examResp.setUncommittedCount( this.getCount( 0, exam.getId() ) );
                examResp.setUncorrectedCount( this.getCount( 1, exam.getId() ) );
                examResp.setCorrectedCount( this.getCount( 2, exam.getId() ) );
            } else if (roleId == 3) { // 如果是学生查询，则只需查询该名学生的测试完成情况
                examResp.setExamStudentStatus( this.examStudentMapper
                        .selectOne( new QueryWrapper<ExamStudent>()
                                .eq( "exam_id", exam.getId() )
                                .eq( "student_id", userId ) )
                        .getStatus() );
            }

            examRespList.add( examResp );
        } );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( examRespList, examRespList.size() ) );
    }

    @Transactional
    @Override
    public CommonResponse updateStatus(long examId, long teacherId, int actionType) {

        // 1. 参数验证
        if (!ObjectUtils.allNotNull( examId, actionType )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 根据 examId 查询 t_exam 的相关记录，然后根据 actionType 执⾏不同操作
        Exam exam = this.examMapper.findExamById( examId );

        if (actionType == 0) {
            // 3. 当 actionType 为 0 时，需要判断 t_exam_question 表⾥有没有相关的试题记录，如果没有，提⽰ “试卷⾥⾯没有试题，发布失败
            List<ExamQuestion> examQuestionList = this.examQuestionMapper.selectList( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId ) );
            if (!ObjectUtils.isNotEmpty( examQuestionList )) {
                return new CommonResponse( ExamCode.UPDATE_EXAM_STATUS_FAIL_NO_QUESTION );
            }
            // 4. actionType 为 0，代表教师执⾏⾸次发布操作，此时修改 status 为 true
            exam.setStatus( true );
        } else if (actionType == 1) {
            // 5. actionType 为 1，代表教师执⾏重新发布操作，此时修改 start_time 为 new Date(), end_time为 null
            exam.setStartTime( new Date() );
            exam.setEndTime( null );
            exam.setStatus( true );
        } else if (actionType == 2) {
            // 6. actionType 为 2，代表教师执⾏取消发布操作，此时修改 status 为 false
            exam.setStatus( false );
        } else if (actionType == 3) {
            // 7.actionType 为 3，代表教师执行结束发布操作，此时修改 end_time 为 new Date()
            exam.setEndTime( new Date() );
        }

        this.examMapper.updateStatus( exam );

        // 如果 actionType 为 0 和 1，发送异步事件提醒，告知学生教师发布了新测试
        if (actionType == 0 || actionType == 1) {
            Long courseId = this.courseMapper.selectById( exam.getCourseId() ).getId();
            this.amqpUtils.sendCourseRemind( teacherId, courseId, examId, "测试（考试）", "发布",
                    "http://www.yixuetang.com/courseDetail.html?id=" + courseId );
        }

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    public CommonResponse updateExam(long examId, InsertExam insertExam) {
        // 1.测试考试是否存在
        Exam exam = examMapper.selectOne( new QueryWrapper<Exam>().eq( "id", examId ) );
        if (exam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        // 2. 参数验证
        if (insertExam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 3. 测试题目非空判断
        if (!StringUtils.isNoneBlank( insertExam.getTitle() )) {
            ExceptionThrowUtils.cast( ExamCode.INSERT_EXAM_FAIL_TITLE_IS_NULL );
        }

        // 4. 测试简介非空判断
        if (!StringUtils.isNoneBlank( insertExam.getIntroduction() )) {
            ExceptionThrowUtils.cast( ExamCode.INSERT_EXAM_FAIL_INTRODUCTION_IS_NULL );
        }
        // 5.根据课程id新建测试
        exam.setTitle( insertExam.getTitle() );
        exam.setIntroduction( insertExam.getIntroduction() );
        exam.setStartTime( insertExam.getStartTime() );
        exam.setEndTime( insertExam.getEndTime() );
        examMapper.updateById( exam );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    public QueryResponse findListByExamId(long examId, long userId) {
        // examId, userId 不合法
        Exam exam = examMapper.selectOne( new QueryWrapper<Exam>().eq( "id", examId ) );
        User user = userMapper.findById( userId );
        if (!ObjectUtils.allNotNull( exam, user )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        long roleId = user.getRole().getId();
        int examStudentStatus = 0;
        if (roleId == 3) {
            // 获取该名学生的答题情况
            examStudentStatus = examStudentMapper.selectOne(
                    new QueryWrapper<ExamStudent>()
                            .eq( "exam_id", examId )
                            .eq( "student_id", userId ) )
                    .getStatus();
            // 学生不允许查看未开始的测试
            if (!exam.getStatus() || new Date().getTime() < exam.getStartTime().getTime()) {
                ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
            }
        }

        // 当且仅当查询者为学生，并且其未提交测试时，禁止查看参考答案和解析
        boolean isForbidden = roleId == 3 && examStudentStatus == 0;

        List<ExamQuestionRequest> examQuestionRespList = new ArrayList<>();

        examQuestionMapper.selectList( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId ) )
                .forEach( examQuestion -> {

                    Long questionId = examQuestion.getQuestionId();
                    Integer questionType = examQuestion.getQuestionType();

                    ExamQuestionRequest examQuestionRequest = ExamQuestionRequest.builder()
                            .questionNumber( examQuestion.getQuestionNumber() )
                            .questionType( questionType )
                            .questionTypeName( getQuestionTypeName( questionType, questionId ) )
                            .score( getQuestionScore( questionType, questionId ) )
                            .content( getQuestionContent( questionType, questionId ) )
                            .singleSelectAnswer( isForbidden ? null : getQuestionSingleSelectAnswer( questionType, questionId ) )
                            .multiSelectAnswer( isForbidden ? null : getQuestionMultiSelectAnswer( questionType, questionId ) )
                            .judgeAnswer( isForbidden ? null : getQuestionJudgeAnswer( questionType, questionId ) )
                            .fillAnswer( isForbidden ? null : getQuestionFillAnswer( questionType, questionId ) )
                            .questionAndAnswer( isForbidden ? null : getQuestionAndAnswer( questionType, questionId ) )
                            .analysis( isForbidden ? null : getQuestionAnalysis( questionType, questionId ) )
                            .selectChoices( getQuestionSelectChoices( questionType, questionId ) )
                            .build();

                    examQuestionRespList.add( examQuestionRequest );
                } );

        examQuestionRespList.sort( Comparator.comparing( ExamQuestionRequest::getQuestionNumber ) );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( examQuestionRespList, examQuestionRespList.size() ) );
    }

    @Transactional
    @Override
    public CommonResponse deleteById(long examId) {

        // 1. 考试（测试）是否存在
        Exam exam = this.examMapper.findExamById( examId );
        if (!ObjectUtils.isNotEmpty( exam )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 根据examId删除t_exam_student的相关记录
        this.examStudentMapper.delete( new QueryWrapper<ExamStudent>().eq( "exam_id", examId ) );

        // 3. 根据examId查询出examQuestionIds列表
        List<Long> examQuestionIds = this.examQuestionMapper.findExamQuestionIds( examId );

        // 4. 根据 examId 删除 t_exam_question_student 的相关记录
        for (Long examQuestionId : examQuestionIds) {
            this.examQuestionStudentMapper.delete( new QueryWrapper<ExamQuestionStudent>().eq( "exam_question_id", examQuestionId ) );
        }

        // 5. 根据 examId 删除 t_exam_quesiton 的相关记录
        this.examQuestionMapper.delete( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId ) );

        // 6. 根据 examId 删除 t_exam 的相关记录
        this.examMapper.deleteById( examId );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    public QueryResponse getExamQuestionStudent(long examId, int questionNumber, long studentId) {

        ExamQuestion examQuestion = checkParamsAndGetExamQuestion( examId, questionNumber, studentId );

        long examQuestionId = examQuestion.getId();

        ExamQuestionStudent examQuestionStudent = getExamQuestionStudent( studentId, examQuestionId );

        List<ExamQuestionStudent> examQuestionStudents =
                examQuestionStudent == null
                        ? new ArrayList<>()
                        : Collections.singletonList( examQuestionStudent );

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( examQuestionStudents, examQuestionStudents.size() ) );
    }

    @Override
    @Transactional
    public CommonResponse saveExamQuestionStudent(long examId, int questionNumber, long studentId, ExamQuestionStudentRequest examQuestionStudentRequest) {

        ExamQuestion examQuestion = checkParamsAndGetExamQuestion( examId, questionNumber, studentId );

        long examQuestionId = examQuestion.getId();

        ExamQuestionStudent foundExamQuestionStudent = getExamQuestionStudent( studentId, examQuestionId );

        // 将该名学生这道题的作答转化成字符串形式（多选题和判断题需要转化）
        String studentAnswer = parseToString( examQuestionStudentRequest );

        // 获取该名学生这道题的得分
        Double studentScore = getStudentScore( examQuestionStudentRequest, examId );

        if (ObjectUtils.isEmpty( foundExamQuestionStudent )) {
            ExamQuestionStudent examQuestionStudent = ExamQuestionStudent.builder()
                    .id( null )
                    .examQuestionId( examQuestionId )
                    .studentId( studentId )
                    .answer( studentAnswer )
                    .score( studentScore )
                    .build();
            examQuestionStudentMapper.insert( examQuestionStudent );
        } else {
            foundExamQuestionStudent.setAnswer( studentAnswer );
            foundExamQuestionStudent.setScore( studentScore );
            examQuestionStudentMapper.updateById( foundExamQuestionStudent );
        }

        // 更新该名学生该次测试的总分
        if (studentScore != null) {
            ExamStudent examStudent = examStudentMapper.selectOne(
                    new QueryWrapper<ExamStudent>()
                            .eq( "exam_id", examId )
                            .eq( "student_id", studentId ) );
            if (examStudent.getScore() != null) {
                studentScore += examStudent.getScore();
            }
            examStudent.setScore( studentScore );
            examStudentMapper.updateById( examStudent );
        }

        return CommonResponse.SUCCESS();
    }

    @Override
    @Transactional
    public CommonResponse submitExam(long examId, long studentId) {
        checkParams( examId, studentId );

        // 将学生的测试状态改为1：待批改
        ExamStudent examStudent = examStudentMapper.selectOne(
                new QueryWrapper<ExamStudent>()
                        .eq( "exam_id", examId )
                        .eq( "student_id", studentId ) );
        examStudent.setStatus( 1 );
        examStudent.setSubmitTime( new Date() );

        setStatusToCompletedIfNeeded( examId, studentId, examStudent );

        // 执行更新操作
        examStudentMapper.update( examStudent,
                new UpdateWrapper<ExamStudent>()
                        .eq( "exam_id", examId )
                        .eq( "student_id", studentId ) );

        return CommonResponse.SUCCESS();
    }

    @Override
    public QueryResponse getExamStudent(long examId, long studentId) {
        checkParams( examId, studentId );

        ExamStudent examStudent = examStudentMapper.selectOne(
                new QueryWrapper<ExamStudent>()
                        .eq( "exam_id", examId )
                        .eq( "student_id", studentId ) );

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( Collections.singletonList( examStudent ), 1 ) );
    }

    @Override
    @Transactional
    public CommonResponse editStudentScore(EditStudentScoreRequest editStudentScoreRequest) {
        Long studentId = editStudentScoreRequest.getStudentId();
        Long examId = editStudentScoreRequest.getExamId();
        Integer questionNumber = editStudentScoreRequest.getQuestionNumber();
        Double studentScore = editStudentScoreRequest.getStudentScore();

        ExamQuestion examQuestion = checkParamsAndGetExamQuestion( examId, questionNumber, studentId );

        ExamQuestionStudent examQuestionStudent = examQuestionStudentMapper.selectOne(
                new QueryWrapper<ExamQuestionStudent>()
                        .eq( "exam_question_id", examQuestion.getId() )
                        .eq( "student_id", studentId ) );
        Double originalScore = examQuestionStudent.getScore(); // 记录原先的得分
        examQuestionStudent.setScore( studentScore );
        examQuestionStudentMapper.updateById( examQuestionStudent );

        // 更新总分和学生测试状态码
        ExamStudent examStudent = examStudentMapper.selectOne(
                new QueryWrapper<ExamStudent>()
                        .eq( "exam_id", examId )
                        .eq( "student_id", studentId ) );
        if (setStatusToCompletedIfNeeded( examId, studentId, examStudent )) {
            Double totalScore = examStudent.getScore();
            if (totalScore != null) {
                totalScore -= originalScore; // 先减掉原先的得分
                totalScore += studentScore; // 再加上更新的得分
            }
            examStudent.setScore( totalScore );
            examStudentMapper.updateById( examStudent );
            return new CommonResponse( ExamCode.CORRECTION_COMPLETED );
        }

        return CommonResponse.SUCCESS();
    }

    private void checkParams(long examId, long studentId) {
        // 参数校验
        Exam exam = examMapper.selectById( examId );

        User user = userMapper.selectById( studentId );

        if (!ObjectUtils.allNotNull( exam, user )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
    }

    private Double getStudentScore(ExamQuestionStudentRequest examQuestionStudentRequest, long examId) {
        String questionTypeName = examQuestionStudentRequest.getQuestionTypeName();
        ExamQuestion examQuestion = examQuestionMapper.selectOne(
                new QueryWrapper<ExamQuestion>()
                        .eq( "exam_id", examId )
                        .eq( "question_number", examQuestionStudentRequest.getQuestionNumber() ) );
        long examQuestionId = examQuestion.getQuestionId();
        switch (questionTypeName) {
            case "单选题":
                Select singleSelect = selectMapper.selectById( examQuestionId );
                String studentSingleSelectAnswer = examQuestionStudentRequest.getStudentSingleSelectAnswer();
                if (Objects.equals( studentSingleSelectAnswer, singleSelect.getAnswer() )) {
                    return singleSelect.getScore();
                }
                return 0.0;
            case "多选题":
                Select multiSelect = selectMapper.selectById( examQuestionId );
                List<String> studentMultiSelectAnswer = examQuestionStudentRequest.getStudentMultiSelectAnswer();
                Collections.sort( studentMultiSelectAnswer );
                if (multiSelect.getAnswer().equals( JSONObject.toJSONString( studentMultiSelectAnswer ) )) {
                    return multiSelect.getScore();
                }
                return 0.0;
            case "判断题":
                Judge judge = judgeMapper.selectById( examQuestionId );
                Boolean studentJudgeAnswer = examQuestionStudentRequest.getStudentJudgeAnswer();
                if (Objects.equals( studentJudgeAnswer, judge.getAnswer() )) {
                    return judge.getScore();
                }
                return 0.0;
            case "填空题":
                Fill fill = fillMapper.selectById( examQuestionId );
                if (StringUtils.isNoneBlank( fill.getAnswer() )) {
                    String studentFillAnswer = examQuestionStudentRequest.getStudentFillAnswer();
                    if (Objects.equals( studentFillAnswer, fill.getAnswer() )) {
                        return fill.getScore();
                    }
                    return 0.0;
                }
                return null;
            default:
                return null;
        }
    }

    private String parseToString(ExamQuestionStudentRequest examQuestionStudentRequest) {
        String questionTypeName = examQuestionStudentRequest.getQuestionTypeName();
        switch (questionTypeName) {
            case "单选题":
                return examQuestionStudentRequest.getStudentSingleSelectAnswer();
            case "多选题":
                List<String> studentMultiSelectAnswer = examQuestionStudentRequest.getStudentMultiSelectAnswer();
                Collections.sort( studentMultiSelectAnswer );
                return JSONObject.toJSONString( studentMultiSelectAnswer );
            case "判断题":
                return JSONObject.toJSONString( examQuestionStudentRequest.getStudentJudgeAnswer() );
            case "填空题":
                return examQuestionStudentRequest.getStudentFillAnswer();
            case "问答题":
                return examQuestionStudentRequest.getStudentQuestionAndAnswer();
            default:
                return null;
        }
    }

    private ExamQuestionStudent getExamQuestionStudent(long studentId, long examQuestionId) {
        return examQuestionStudentMapper.selectOne(
                new QueryWrapper<ExamQuestionStudent>()
                        .eq( "exam_question_id", examQuestionId )
                        .eq( "student_id", studentId ) );
    }

    private Integer getCount(int status, Long examId) {
        return this.examStudentMapper
                .selectCount( new QueryWrapper<ExamStudent>()
                        .eq( "exam_id", examId )
                        .eq( "status", status ) );
    }

    private String getQuestionTypeName(Integer questionType, Long questionId) {
        switch (questionType) {
            case 0:
                Select select = this.selectMapper.selectById( questionId );
                return select.getType() == 0 ? "单选题" : "多选题";
            case 1:
                return "判断题";
            case 2:
                return "填空题";
            case 3:
                return "问答题";
            default:
                return null;
        }
    }

    private Double getQuestionScore(Integer questionType, Long questionId) {
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

    private String getQuestionContent(Integer questionType, Long questionId) {
        switch (questionType) {
            case 0:
                return this.selectMapper.selectById( questionId ).getContent();
            case 1:
                return this.judgeMapper.selectById( questionId ).getContent();
            case 2:
                return this.fillMapper.selectById( questionId ).getContent();
            case 3:
                return this.qaMapper.selectById( questionId ).getContent();
            default:
                return null;
        }
    }

    private String getQuestionAnalysis(Integer questionType, Long questionId) {
        switch (questionType) {
            case 0:
                return this.selectMapper.selectById( questionId ).getAnalysis();
            case 1:
                return this.judgeMapper.selectById( questionId ).getAnalysis();
            case 2:
                return this.fillMapper.selectById( questionId ).getAnalysis();
            case 3:
                return this.qaMapper.selectById( questionId ).getAnalysis();
            default:
                return null;
        }
    }

    private String getQuestionSingleSelectAnswer(Integer questionType, Long selectId) {
        return StringUtils.equals( getQuestionTypeName( questionType, selectId ), "单选题" )
                ? this.selectMapper.selectById( selectId ).getAnswer()
                : null;
    }

    private List<String> getQuestionMultiSelectAnswer(Integer questionType, Long selectId) {
        return StringUtils.equals( getQuestionTypeName( questionType, selectId ), "多选题" )
                ? JSONObject.parseArray( this.selectMapper.selectById( selectId ).getAnswer() ).toJavaList( String.class )
                : null;
    }

    private Boolean getQuestionJudgeAnswer(Integer questionType, Long judgeId) {
        return StringUtils.equals( getQuestionTypeName( questionType, judgeId ), "判断题" )
                ? this.judgeMapper.selectById( judgeId ).getAnswer()
                : null;
    }

    private String getQuestionFillAnswer(Integer questionType, Long fillId) {
        return StringUtils.equals( getQuestionTypeName( questionType, fillId ), "填空题" )
                ? this.fillMapper.selectById( fillId ).getAnswer()
                : null;
    }

    private String getQuestionAndAnswer(Integer questionType, Long qaId) {
        return StringUtils.equals( getQuestionTypeName( questionType, qaId ), "问答题" )
                ? this.qaMapper.selectById( qaId ).getAnswer()
                : null;
    }

    private List<SelectChoice> getQuestionSelectChoices(Integer questionType, Long selectId) {
        return Objects.equals( questionType, 0 )
                ? JSONObject.parseArray( this.selectMapper.selectById( selectId ).getChoices(), SelectChoice.class )
                : null;
    }

    private ExamQuestion checkParamsAndGetExamQuestion(long examId, int questionNumber, long studentId) {
        Exam exam = examMapper.selectById( examId );

        ExamQuestion examQuestion = examQuestionMapper.selectOne(
                new QueryWrapper<ExamQuestion>()
                        .eq( "exam_id", examId )
                        .eq( "question_number", questionNumber ) );

        User user = userMapper.selectById( studentId );

        // 参数校验
        if (!ObjectUtils.allNotNull( exam, examQuestion, user )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        return examQuestion;
    }

    private boolean setStatusToCompletedIfNeeded(long examId, long studentId, ExamStudent examStudent) {
        Set<Long> examQuestionIdSet = examQuestionMapper.selectList(
                new QueryWrapper<ExamQuestion>()
                        .eq( "exam_id", examId ) )
                .stream()
                .map( ExamQuestion::getId )
                .collect( Collectors.toSet() );

        // 试卷总题数
        int totalQuestionCount = examQuestionMapper.selectCount( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId ) );

        // 该名学生已有分数的题目数
        int scoredCount =
                examQuestionStudentMapper.selectList(
                        new QueryWrapper<ExamQuestionStudent>()
                                .eq( "student_id", studentId ) )
                        .stream()
                        .filter( examQuestionStudent ->
                                examQuestionIdSet.contains( examQuestionStudent.getExamQuestionId() )
                                        && examQuestionStudent.getScore() != null )
                        .collect( Collectors.toList() )
                        .size();

        // 如果所有题目都有分数，将学生的测试状态改为2：已批改
        if (scoredCount == totalQuestionCount) {
            examStudent.setStatus( 2 );
            return true;
        }

        return false;
    }

}
