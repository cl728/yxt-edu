package com.yixuetang.homework.service;

import com.yixuetang.entity.request.homework.InsertHomework;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/11 16:55
 */
public interface HomeworkService {

    /**
     * 查询某门课程下的作业列表
     *
     * @param courseId 课程id
     * @param userId   用户id
     * @return 响应结果实体类
     */
    QueryResponse findByCourseId(long courseId, long userId);

    /**
     * 根据课程id新增作业
     *
     * @param courseId       课程id
     * @param teacherId      教师id
     * @param insertHomework 新增作业实体类
     * @return 通用相应结果实体类
     */
    CommonResponse saveByCourseId(long courseId, long teacherId, InsertHomework insertHomework);
}
