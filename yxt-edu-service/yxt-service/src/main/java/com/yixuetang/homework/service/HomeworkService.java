package com.yixuetang.homework.service;

import com.yixuetang.entity.homework.ScoreStudentHomework;
import com.yixuetang.entity.request.homework.InsertHomework;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

import java.util.List;

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

    /**
     * 学生提交作业
     *
     * @param homeworkId  作业id
     * @param studentId   学生id
     * @param resourceIds 资源id
     * @return 通用响应实体类
     */
    CommonResponse submitHomework(long homeworkId, long studentId, List<Long> resourceIds);

    /**
     * 学生作业评分
     *
     * @param homeworkId           作业id
     * @param studentId            学生id
     * @param scoreStudentHomework 教师评分作业实体类
     * @return
     */
    CommonResponse scoreHomework(long homeworkId, long studentId, ScoreStudentHomework scoreStudentHomework);

    /**
     * 查询某个学生某次作业的提交情况
     *
     * @param homeworkId 作业id
     * @param studentId  学生id
     * @return 通用响应实体类
     */
    QueryResponse findStudentHomeworkSubmit(long homeworkId, long studentId);

    /**
     * 查询某个学生在某个课程下的作业完成情况
     *
     * @param courseId  课程id
     * @param studentId 学生id
     * @return 通用响应实体类
     */
    QueryResponse findStudentCourseHomework(long courseId, long studentId);
}
