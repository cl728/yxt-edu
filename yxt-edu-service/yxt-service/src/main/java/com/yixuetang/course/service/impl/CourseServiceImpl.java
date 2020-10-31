package com.yixuetang.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.course.CourseCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.Role;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.RoleMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.course.GenCodeUtils;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块服务层接口实现类
 * @date 2020/10/29 14:20
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ScMapper scMapper;

    @Override
    public QueryResponse findAllCourses() {
        List<Course> courses = this.courseMapper.selectList( null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( courses, courses.size() ) );
    }

    @Override
    public CommonResponse deleteCourse(Long id) {
        int i = this.courseMapper.deleteById( id );
        if (i < 1) {
            return new CommonResponse( CourseCode.DELETE_COURSE_FAIL );
        }
        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    public CommonResponse joinCourse(Long studentId, String code) {
        // 通过加课码查询课程
        final Course course = this.courseMapper.selectOne( new QueryWrapper<Course>().eq( "c_code", code ) );
        if (course == null) {
            return new CommonResponse( CourseCode.COURSE_NOT_FOUND );
        } else {
            // 判断是否重复加课
            int i = this.scMapper.selectByStudentIdAndCourseId( studentId, course.getId() );
            if (i < 1) {
                this.scMapper.joinCourse( studentId, course.getId() );
                return new CommonResponse( CommonCode.SUCCESS );
            } else {
                return new CommonResponse( CourseCode.JOIN_COURSE_FAIL );
            }
        }
    }

    @Override
    @Transactional
    public CommonResponse saveCourse(Long teacherId, InsertCourse insertCourse) {
        if (insertCourse == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        // 校验teacherId是否合法
        User findUser = userMapper.findById( teacherId );
        Role findRole = roleMapper.selectOne( new QueryWrapper<Role>().eq( "id", findUser.getRole().getId() ) );
        if (findRole == null || findRole.getId() != 2) {
            ExceptionThrowUtils.cast( CourseCode.INSERT_COURSE_FAIL );
        }

        Course course = new Course();

        course.setTeacherId( teacherId );
        course.setCName( insertCourse.getCName() );
        course.setSchoolYear( insertCourse.getSchoolYear() );
        course.setSemester( insertCourse.getSemester() );

        // 生成加课码
        course.setCCode( GenCodeUtils.genRandomCode() );

        // set createTime updateTime
        Date date = new Date();
        course.setCreateTime( date );
        course.setUpdateTime( date );

        this.courseMapper.insert( course );
        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    public QueryResponse findByPage(long currentPage, long pageSize) {
        Page<Course> page = this.courseMapper.selectPage( new Page<>( currentPage, pageSize ), null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( page.getRecords(), (int) page.getTotal() ) );
    }


}
