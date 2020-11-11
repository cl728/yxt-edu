package com.yixuetang.homework.service;

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
}
