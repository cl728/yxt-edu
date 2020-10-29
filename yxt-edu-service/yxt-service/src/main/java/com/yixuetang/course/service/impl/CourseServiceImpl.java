package com.yixuetang.course.service.impl;

import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
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

    @Override
    public QueryResponse findAllCourses() {
        List<Course> courses = this.courseMapper.selectList(null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult(courses, courses.size()));
    }
}
