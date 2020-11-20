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

    /**
     * 根据作业id删除作业
     *
     * @param homeworkId 作业id
     * @param courseId   课程id
     * @return 通用响应实体类
     */
    CommonResponse deleteByHomeworkId(long homeworkId, long courseId);

    /**
     * 根据作业id查询作业信息
     *
     * @param homeworkId 作业Id
     * @return 带查询结果集的响应实体类
     */
    QueryResponse findById(long homeworkId);

    /**
     * 教师编辑作业
     *
     * @param homeworkId     作业id
     * @param insertHomework 编辑作业实体类
     * @return 通用响应实体类
     */
    CommonResponse updateHomework(long homeworkId, InsertHomework insertHomework);

    /**
     * 根据作业id切换置顶字段
     *
     * @param homeworkId 作业id
     * @return 通用响应实体类
     */
    CommonResponse switchTopNum(long homeworkId);

    /**
     * 根据课程id查询该门课程下的全部作业成绩信息
     *
     * @param courseId  课程id
     * @param teacherId 教师id
     * @return 带结果集的响应实体类
     */
    QueryResponse findScoresByCourseId(long courseId, long teacherId);
}
