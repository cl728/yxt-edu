package com.yixuetang.course.service.impl;

import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.course.StudentCourse;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.course.CourseCode;
import com.yixuetang.entity.response.result.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public CommonResponse joinCourse(Long studentId, Long courseId) {
        // 判断课程是否存在
        Course course = this.courseMapper.selectById( courseId );
        if (course == null) {
            return new CommonResponse( CourseCode.COURSE_NOT_FOUND );
        } else {
            // 判断是否重复加课
            int i = this.scMapper.selectByStudentIdAndCourseId( studentId, courseId );
            if (i < 1) {
                this.scMapper.joinCourse( studentId, courseId );
                return new CommonResponse( CommonCode.SUCCESS );
            } else {
                return new CommonResponse( CourseCode.JOIN_COURSE_FAIL );
            }
        }
    }


}
