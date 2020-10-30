package com.yixuetang.course.service;

import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块服务层接口
 * @date 2020/10/29 11:19
 */
public interface CourseService {

    /**
     * 查询所有学校
     *
     * @return 查询结果实体类
     */
    QueryResponse findAllCourses();

    /**
     * 删除一门课程
     *
     * @return 响应结果实体类
     */
    CommonResponse deleteCourse(Long id);

    /**
     * 加入课程
     *
     * @param studentId 学生id
     * @param code  加课码
     * @return  响应结果实体类
     */
    CommonResponse joinCourse(Long studentId, String code);
}
