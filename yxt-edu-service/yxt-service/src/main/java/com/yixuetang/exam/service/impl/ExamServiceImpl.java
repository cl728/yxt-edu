package com.yixuetang.exam.service.impl;

import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.exam.Exam;
import com.yixuetang.entity.request.exam.InsertExam;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.exam.ExamCode;
import com.yixuetang.entity.response.code.homework.HomeworkCode;
import com.yixuetang.entity.user.User;
import com.yixuetang.exam.mapper.ExamMapper;
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
}
