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
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.request.exam.question.ExamQuestionRequest;
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
            select.setAnswer( select.getType() == 0 ? examQuestionRequest.getSingleSelectAnswer() : JSONObject.toJSONString( examQuestionRequest.getMultiSelectAnswer() ) );
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
        //测试考试是否存在
        Exam exam = examMapper.selectOne( new QueryWrapper<Exam>().eq( "id", examId ) );
        if (exam == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        //题目是否存在
        ExamQuestion examQuestion = examQuestionMapper.selectOne( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId )
                .eq( "question_number", questionNumber ) );
        if (examQuestion == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        //根据 examId, questionNumber 删除 t_exam_quesiton 的相关记录
        examQuestionMapper.delete( new QueryWrapper<ExamQuestion>().eq( "exam_id", examId )
                .eq( "question_number", questionNumber ) );

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

        // 学生不允许查看未开始的测试
        if (user.getRole().getId() == 3) {
            if (!exam.getStatus() || new Date().getTime() < exam.getStartTime().getTime()) {
                ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
            }
        }

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
                            .singleSelectAnswer( getQuestionSingleSelectAnswer( questionType, questionId ) )
                            .multiSelectAnswer( getQuestionMultiSelectAnswer( questionType, questionId ) )
                            .judgeAnswer( getQuestionJudgeAnswer( questionType, questionId ) )
                            .fillAnswer( getQuestionFillAnswer( questionType, questionId ) )
                            .questionAndAnswer( getQuestionAndAnswer( questionType, questionId ) )
                            .analysis( getQuestionAnalysis( questionType, questionId ) )
                            .selectChoices( getQuestionSelectChoices( questionType, questionId ) )
                            .build();

                    examQuestionRespList.add( examQuestionRequest );
                } );

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

}
