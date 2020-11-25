package com.yixuetang.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.entity.auth.UserInfo;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.homework.Homework;
import com.yixuetang.entity.homework.HomeworkResource;
import com.yixuetang.entity.homework.HomeworkStudent;
import com.yixuetang.entity.request.homework.InsertHomework;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.course.CourseCode;
import com.yixuetang.entity.response.code.homework.HomeworkCode;
import com.yixuetang.entity.response.result.HomeworkResp;
import com.yixuetang.entity.response.result.HomeworkScoreResp;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.homework.mapper.HomeworkMapper;
import com.yixuetang.homework.mapper.HomeworkResourceMapper;
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

import java.util.*;

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

    @Autowired
    private HomeworkResourceMapper homeworkResourceMapper;

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

        List<Homework> homeworkList = this.homeworkMapper.selectList(new QueryWrapper<Homework>()
                .eq("course_id", courseId)
                .orderByDesc("top_num"));

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

    @Override
    public CommonResponse deleteByHomeworkId(long homeworkId, long courseId) {
        //  根据作业id查询作业
        Homework homework = this.homeworkMapper.selectById(homeworkId);
        //  查询不到作业
        if (homework == null) {
            return new CommonResponse(HomeworkCode.HOMEWORK_NOT_EXIST);
        }
        //  验证课程id
        if (homework.getCourseId() != courseId) {
            return new CommonResponse(HomeworkCode.HOMEWORK_IS_NOT_BELONG_TO_THIS_COURSE);
        }

        //  执行t_hs表删除对应作业id的作业操作
        this.homeworkStudentMapper.delete(new QueryWrapper<HomeworkStudent>().eq("homework_id", homeworkId));
        //  执行t_homework表删除作业操作
        this.homeworkMapper.deleteById(homeworkId);
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse updateHomework(long homeworkId, InsertHomework insertHomework) {
        //  根据作业id查询作业
        Homework homework = this.homeworkMapper.selectById(homeworkId);
        //  判断作业是否存在
        if (homework == null) {
            return new CommonResponse(HomeworkCode.HOMEWORK_NOT_EXIST);
        }
        //  判断作业标题是否为空
        if (!StringUtils.isNoneBlank(insertHomework.getTitle())) {
            return new CommonResponse(HomeworkCode.UPDATE_HOMEWORK_FAIL_TITLE_IS_NULL);
        }

        //  判断作业描述是否为空
        if (!StringUtils.isNoneBlank(insertHomework.getDescription())) {
            return new CommonResponse(HomeworkCode.UPDATE_HOMEWORK_FAIL_DESCRIPTION_IS_NULL);
        }

        //  截止时间至少比当前时间大30分钟
        if (ObjectUtils.isEmpty(insertHomework.getDeadline())) {
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_DEADLINE_IS_NULL);
        }
        Date dateAdd30Mins = DateUtils.addMinutes(new Date(), 30);
        int truncatedCompareTo = DateUtils.truncatedCompareTo(insertHomework.getDeadline(), dateAdd30Mins, Calendar.SECOND);
        if (truncatedCompareTo == -1) {
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_DEADLINE_LESS_THAN_30_MINS);
        }

        //  总分值>0 <1
        if (insertHomework.getTotalScore() <= 0 || insertHomework.getTotalScore() > 100) {
            return new CommonResponse(HomeworkCode.INSERT_HOMEWORK_FAIL_TOTAL_SCORE_INVAILD);
        }

        //  把编辑内容存入Homework实体类
        homework.setTitle(insertHomework.getTitle());
        homework.setDescription(insertHomework.getDescription());
        homework.setDeadline(insertHomework.getDeadline());
        homework.setTotalScore(insertHomework.getTotalScore());
        this.homeworkMapper.updateById(homework);
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse switchTopNum(long homeworkId) {
        //  作业是否存在
        Homework homework = this.homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }
        homework.setTopNum(homework.getTopNum() == 0 ? 10 : 0);
        this.homeworkMapper.updateById(homework);
        return CommonResponse.SUCCESS();
    }

    @Override
    public QueryResponse findScoresByCourseId(long courseId, long teacherId) {

        // 1. 根据课程id查询出已有课程信息
        Course course = this.courseMapper.findById(courseId);
        if (course == null) {
            //找不到此课程
            return new QueryResponse(CourseCode.COURSE_NOT_FOUND, null);
        }

        // 2. 判断该用户是否为该课程的授课教师
        if (course.getTeacher().getId() != teacherId) {
            //查询失败，该用户不是该课程的授课老师！
            return new QueryResponse(CourseCode.FIND_COURSE_SCORES_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER, null);
        }

        // 3. 查询该课程下的所有作业信息列表
        List<Homework> homeworkList = this.homeworkMapper.selectList(new QueryWrapper<Homework>().eq("course_id", courseId));

        // 4. 查询每一次作业下的学生成绩信息列表
        List<List<HomeworkScoreResp>> homeworkScoreRespAllList = new ArrayList<>();
        for (Homework homework : homeworkList) {
            List<HomeworkStudent> homeworkStudentList = this.homeworkStudentMapper.selectList(new QueryWrapper<HomeworkStudent>().eq("homework_id", homework.getId()));
            List<HomeworkScoreResp> homeworkScoreRespList = new ArrayList<>();
            for (HomeworkStudent homeworkStudent : homeworkStudentList) {
                //依次获取HomeworkStudent，然后复制属性到HomeworkScoreResp，并设置学号、头像、标题和总分值
                HomeworkScoreResp homeworkScoreResp = new HomeworkScoreResp();
                BeanUtils.copyProperties(homeworkStudent, homeworkScoreResp);
                User student = this.userMapper.findById(homeworkStudent.getStudentId());
                homeworkScoreResp.setRealName(student.getRealName());
                homeworkScoreResp.setTsNo(student.getTsNo());
                homeworkScoreResp.setAvatar(student.getAvatar());
                homeworkScoreResp.setTitle(homework.getTitle());
                homeworkScoreResp.setTotalScore(homework.getTotalScore());
                homeworkScoreRespList.add(homeworkScoreResp);
            }
            homeworkScoreRespAllList.add(homeworkScoreRespList);
        }

        // 5. 把数据封装到map里
        Map<Long, List<HomeworkScoreResp>> homeworkScoreRespMap = new HashMap<>();
        for (List<HomeworkScoreResp> homeworkScoreRespList : homeworkScoreRespAllList) {
            for (HomeworkScoreResp homeworkScoreResp : homeworkScoreRespList) {
                //首先创建只包含key（studentId）的map
                homeworkScoreRespMap.put(homeworkScoreResp.getStudentId(), new ArrayList<>());
            }
        }

        // 6. 根据studentId把map中对应key的value（homeworkScoreResp的ArrayList）逐次添加
        for (List<HomeworkScoreResp> homeworkScoreRespList : homeworkScoreRespAllList) {
            for (HomeworkScoreResp homeworkScoreResp : homeworkScoreRespList) {
                List<HomeworkScoreResp> homeworkScoreRespList1 = homeworkScoreRespMap.get(homeworkScoreResp.getStudentId());
                homeworkScoreRespList1.add(homeworkScoreResp);
                homeworkScoreRespMap.put(homeworkScoreResp.getStudentId(), homeworkScoreRespList1);
            }
        }

        // 7. 为了返回List类型的QueryResult，把map用一个list封装起来
        List<Map<Long, List<HomeworkScoreResp>>> mapList = new ArrayList<>();
        mapList.add(homeworkScoreRespMap);

        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(mapList, mapList.size()));
    }

    @Override
    public QueryResponse findById(long homeworkId) {
        //  作业是否存在
        Homework homework = this.homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(Collections.singletonList(homework), 1));

    }

    @Override
    public CommonResponse submitHomework(long homeworkId, long studentId, List<Long> resourceIds) {
        //判断作业是否存在
        Homework homework = homeworkMapper.selectOne(new QueryWrapper<Homework>().eq("id", homeworkId));
        if(homework == null){
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }
        //学生是否存在
        User user = userMapper.findById(studentId);
        if(user == null || user.getRole().getId() != 3){
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }
        //resourceIds是否为空
        if(resourceIds == null){
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        //HomeworkStudent记录是否存在
        HomeworkStudent homeworkStudent = homeworkStudentMapper.selectOne(new QueryWrapper<HomeworkStudent>().eq("homework_id", homeworkId).eq("student_id", studentId));
        if(homeworkStudent == null){
            return new CommonResponse(HomeworkCode.STUDENT_HOMEWORK_NOT_EXIST);
        }

        //修改 t_homework_student 记录的相关字段（submit_time, update_time, status）
        Date date = new Date();
        if(homeworkStudent.getSubmitTime() == null){
            homeworkStudent.setSubmitTime(date);
        }
        homeworkStudent.setUpdateTime(date);
        homeworkStudent.setStatus(homeworkStudent.getStatus()+1);
        homeworkStudentMapper.updateById(homeworkStudent);

        //在 t_homework_resource 里新增相关记录
        for (Long resourceId : resourceIds) {
            HomeworkResource homeworkResource  = new HomeworkResource();
            homeworkResource.setHomeworkId(homeworkId);
            homeworkResource.setStudentId(studentId);
            homeworkResource.setResourceId(resourceId);
            homeworkResourceMapper.insert(homeworkResource);
        }

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse scoreHomework(long homeworkId, long studentId, double score) {
        //判断作业是否存在
        Homework homework = homeworkMapper.selectOne(new QueryWrapper<Homework>().eq("id", homeworkId));
        HomeworkStudent homeworkStudent = homeworkStudentMapper.selectOne(new QueryWrapper<HomeworkStudent>().eq("homework_id", homeworkId)
                .eq("student_id", studentId));
        if(homework == null){
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }
        //判断学生是否有该作业
        if (homeworkStudent == null){
            ExceptionThrowUtils.cast(HomeworkCode.STUDENT_HOMEWORK_NOT_EXIST);
        }
        //判断评分分值是否超出范围
        if (score < 0){
            ExceptionThrowUtils.cast(HomeworkCode.THE_SCORE_CAN_NOT_BE_LOWER_THAN_ZERO);
        }
        if (score > homework.getTotalScore()){
            ExceptionThrowUtils.cast(HomeworkCode.THE_SCORE_EXCEEDS_THE_MAXIMUM);
        }

        homeworkStudent.setScore(score);
        homeworkStudent.setStatus(2);
        homeworkStudent.setCorrectCount(homeworkStudent.getCorrectCount() + 1);
        homeworkStudentMapper.updateById(homeworkStudent);
        return new CommonResponse(CommonCode.SUCCESS);
    }


    private Integer getCount(int countType, long courseId) {
        return this.homeworkStudentMapper
                .selectCount(new QueryWrapper<HomeworkStudent>()
                        .eq("homework_id", courseId)
                        .eq("status", countType));
    }
}
