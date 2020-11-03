package com.yixuetang.course.service;

import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.request.course.TransferCourse;
import com.yixuetang.entity.request.course.UpdateCourse;
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
     * @param teacherId 教师id
     * @param courseId  课程id
     * @return 响应结果实体类
     */
    CommonResponse deleteCourse(Long teacherId, Long courseId);

    /**
     * 加入课程
     *
     * @param userId 用户id
     * @param code   加课码
     * @return 响应结果实体类
     */
    CommonResponse joinCourse(Long userId, String code);

    /**
     * 添加课程
     *
     * @param teacherId 教师id
     * @param course    添加课程实体类
     * @return 响应结果实体类
     */
    CommonResponse saveCourse(Long teacherId, InsertCourse course);

    /**
     * 分页查询课程
     *
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 查询结果实体类
     */
    QueryResponse findByPage(long currentPage, long pageSize);

    /**
     * 查询用户课程列表
     *
     * @param userId 用户id
     * @return 查询结果实体类
     */
    QueryResponse findCoursesByUserId(long userId);


    /**
     * 课程置顶/取消置顶
     *
     * @param courseId
     * @param userId
     * @return 响应结果实体类
     */
    CommonResponse updateTopCourse(long courseId, long userId);

    /**
     * 教师转让课程
     *
     * @param courseId       课程id
     * @param teacherId      教师id
     * @param transferCourse 转让课程实体类
     * @return 响应结果实体类
     */
    CommonResponse transferCourses(Long courseId, Long teacherId, TransferCourse transferCourse);

    /**
     * @param courseId     课程id
     * @param teacherId    教师id
     * @param updateCourse 修改课程信息实体类
     * @return 响应结果实体类
     */
    CommonResponse updateCourses(Long courseId, Long teacherId, UpdateCourse updateCourse);

    /**
     * 归档
     *
     * @param courseId
     * @param userId
     * @return 响应结果实体类
     */
    CommonResponse file(long courseId, long userId);
}
