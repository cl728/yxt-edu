package com.yixuetang.course.service;

import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.request.course.QueryPageRequestCourse;
import com.yixuetang.entity.request.course.TransferCourse;
import com.yixuetang.entity.request.course.UpdateCourse;
import com.yixuetang.entity.request.user.DelCourseUser;
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
     * 删除课程/退课
     *
     * @param courseId      课程id
     * @param delCourseUser 删除课程/退课的用户实体类
     * @return 响应结果实体类
     */
    CommonResponse deleteCourse(Long courseId, DelCourseUser delCourseUser);

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
     * @param currentPage            当前页
     * @param pageSize               每页数量
     * @param queryPageRequestCourse 课程管理页面分页查询实体类
     * @return 查询结果实体类
     */
    QueryResponse findByPage(long currentPage, long pageSize, QueryPageRequestCourse queryPageRequestCourse);

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
     * @param courseId 课程id
     * @param userId   用户id
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
     * @param courseId 课程id
     * @param userId   用户id
     * @return 响应结果实体类
     */
    CommonResponse file(long courseId, long userId);

    /**
     * 根据课程id查询课程信息
     *
     * @param courseId 课程信息
     * @return 查询结果实体类
     */
    QueryResponse findById(long courseId);

    /**
     * 管理员删除某门课程
     *
     * @param courseId 课程id
     * @return 响应结果实体类
     */
    CommonResponse delById(long courseId);

    /**
     * 重置加课码
     *
     * @param courseId  课程id
     * @return  响应结果实体类
     */
    CommonResponse resetCourseCode(long courseId);
}
