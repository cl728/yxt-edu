package com.yixuetang.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.homework.Homework;
import com.yixuetang.entity.homework.HomeworkStudent;
import com.yixuetang.entity.request.homework.InsertHomework;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.homework.HomeworkCode;
import com.yixuetang.entity.response.result.HomeworkResp;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.homework.mapper.HomeworkMapper;
import com.yixuetang.homework.mapper.HomeworkStudentMapper;
import com.yixuetang.homework.service.HomeworkService;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/11 16:55
 */
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private HomeworkStudentMapper homeworkStudentMapper;

    @Autowired
    private ScMapper scMapper;

    @Override
    public QueryResponse findByCourseId(long courseId, long userId) {

        // courseId 不合法
        Course course = this.courseMapper.selectById(courseId);
        if (course == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // userId 不合法
        User user = this.userMapper.findById(userId);
        if (user == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        List<Homework> homeworkList = this.homeworkMapper.selectList(new QueryWrapper<Homework>().eq("course_id", courseId));

        List<HomeworkResp> homeworkRespList = new ArrayList<>(homeworkList.size());

        homeworkList.forEach(homework -> {
            HomeworkResp homeworkResp = new HomeworkResp();

            BeanUtils.copyProperties(homework, homeworkResp);

            long roleId = user.getRole().getId();
            if (roleId == 2) { // 如果是教师查询，则统计未提交、待批改、已批改人数
                homeworkResp.setUncommittedCount(this.getCount(0, homework.getId()));
                homeworkResp.setUncorrectedCount(this.getCount(1, homework.getId()));
                homeworkResp.setCorrectedCount(this.getCount(2, homework.getId()));
            } else if (roleId == 3) { // 如果是学生查询，则只需查询该名学生的作业完成情况
                homeworkResp.setStatus(this.homeworkStudentMapper
                        .selectOne(new QueryWrapper<HomeworkStudent>()
                                .eq("homework_id", homework.getId())
                                .eq("student_id", userId))
                        .getStatus());
            }

            homeworkRespList.add(homeworkResp);
        });

        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(homeworkRespList, homeworkRespList.size()));
    }

    @Transactional
    @Override
    public CommonResponse saveByCourseId(long courseId, long teacherId, InsertHomework insertHomework) {

        // 1. courseId 不合法
        Course course = this.courseMapper.findById(courseId);
        if (course == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 参数验证
        if (insertHomework == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 3. 判断当前用户是否为该课程教师
        User oldTeacher = this.userMapper.findById(teacherId);
        if (ObjectUtils.notEqual(oldTeacher.getId(), course.getTeacher().getId())) {
            //若该教师不是该课程授课老师，则不允许新增作业
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER);
        }

        // 4. 作业题目非空判断
        if (!StringUtils.isNoneBlank(insertHomework.getTitle())) {
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_TITLE_IS_NULL);
        }

        // 5. 截止时间至少比当前时间大30分钟
        if (ObjectUtils.isEmpty(insertHomework.getDeadline())) {
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_DEADLINE_IS_NULL);
        }
        Date dateAdd30Mins = DateUtils.addMinutes(new Date(), 30);
        int truncatedCompareTo = DateUtils.truncatedCompareTo(insertHomework.getDeadline(), dateAdd30Mins, Calendar.SECOND);
        if (truncatedCompareTo == -1) {
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_DEADLINE_LESS_THAN_30_MINS);
        }

        // 6. 总分值>0 <1
        if (insertHomework.getTotalScore() <= 0 || insertHomework.getTotalScore() > 100) {
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_TOTAL_SCORE_INVAILD);
        }

        // 7. 根据课程id创建作业
        Homework homework = new Homework();
        homework.setCourseId(courseId);
        homework.setCreateTime(new Date());
        homework.setDeadline(insertHomework.getDeadline());
        homework.setDescription(insertHomework.getDescription());
        homework.setTitle(insertHomework.getTitle());
        homework.setSubmitCount(0);
        homework.setTotalScore(insertHomework.getTotalScore());
        this.homeworkMapper.insert(homework);

        // 8. 根据课程id查询选修了该课程的所有学生
        List<Long> studentIds = this.scMapper.findStudentIdByCourseId(courseId);

        // 9. 根据刚创建的作业id新建作业与学生表记录
        for (Long studentId : studentIds) {
            HomeworkStudent homeworkStudent = new HomeworkStudent();
            homeworkStudent.setHomeworkId(homework.getId());
            homeworkStudent.setStudentId(studentId);
            this.homeworkStudentMapper.insert(homeworkStudent);
        }

        return new CommonResponse(CommonCode.SUCCESS);
    }

    private Integer getCount(int countType, long courseId) {
        return this.homeworkStudentMapper
                .selectCount(new QueryWrapper<HomeworkStudent>()
                        .eq("homework_id", courseId)
                        .eq("status", countType));
    }
}
