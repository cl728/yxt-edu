package com.yixuetang.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.homework.Homework;
import com.yixuetang.entity.homework.HomeworkStudent;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.result.HomeworkResp;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.homework.mapper.HomeworkMapper;
import com.yixuetang.homework.mapper.HomeworkStudentMapper;
import com.yixuetang.homework.service.HomeworkService;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/11 16:55
 */
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private HomeworkStudentMapper homeworkStudentMapper;

    @Override
    public QueryResponse findByCourseId(long courseId, long userId) {

        // courseId 不合法
        Course course = this.courseMapper.selectById( courseId );
        if (course == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // userId 不合法
        User user = this.userMapper.findById( userId );
        if (user == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        List<Homework> homeworkList = this.homeworkMapper.selectList( new QueryWrapper<Homework>().eq( "course_id", courseId ) );

        List<HomeworkResp> homeworkRespList = new ArrayList<>( homeworkList.size() );

        homeworkList.forEach( homework -> {
            HomeworkResp homeworkResp = new HomeworkResp();

            BeanUtils.copyProperties( homework, homeworkResp );

            long roleId = user.getRole().getId();
            if (roleId == 2) { // 如果是教师查询，则统计未提交、待批改、已批改人数
                homeworkResp.setUncommittedCount( this.getCount( 0, homework.getId() ) );
                homeworkResp.setUncorrectedCount( this.getCount( 1, homework.getId() ) );
                homeworkResp.setCorrectedCount( this.getCount( 2, homework.getId() ) );
            } else if (roleId == 3) { // 如果是学生查询，则只需查询该名学生的作业完成情况
                homeworkResp.setStatus( this.homeworkStudentMapper
                        .selectOne( new QueryWrapper<HomeworkStudent>()
                                .eq( "homework_id", homework.getId() )
                                .eq( "student_id", userId ) )
                        .getStatus() );
            }

            homeworkRespList.add( homeworkResp );
        } );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( homeworkRespList, homeworkRespList.size() ) );
    }

    private Integer getCount(int countType, long courseId) {
        return this.homeworkStudentMapper
                .selectCount( new QueryWrapper<HomeworkStudent>()
                        .eq( "homework_id", courseId )
                        .eq( "status", countType ) );
    }
}
