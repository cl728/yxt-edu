package com.yixuetang.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.course.StudentCourse;
import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.course.CourseCode;
import com.yixuetang.entity.response.code.user.UserCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.Role;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.RoleMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.course.GenCodeUtils;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ScMapper scMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Override
    public QueryResponse findAllCourses() {
        List<Course> courses = this.courseMapper.selectList(null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(courses, courses.size()));
    }

    @Override
    @Transactional
    public CommonResponse deleteCourse(Long userId, Long courseId) {
        // 先判断该id为学生还是老师
        User user = this.userMapper.findById(userId);

        // roleId异常
        if (user.getRole().getId() <= 1 || user.getRole().getId() > 3) {
            return new CommonResponse(CommonCode.INVALID_PARAM);
        }
        // 判断为教师用户
        if (user.getRole().getId() == 2) {
            // 确认该课程为该名老师所有
            Course course = this.courseMapper.selectOne(new QueryWrapper<Course>().eq("id", courseId)
                    .eq("teacher_id", userId).select("id"));
            if (course == null) {
                return new CommonResponse(CommonCode.INVALID_PARAM);
            }

            try {
                // 将选课表里关于该课程的记录删除
                this.scMapper.delete(new QueryWrapper<StudentCourse>().eq("course_id", courseId));

                // 将该课程删除
                this.courseMapper.deleteById(courseId);
            } catch (Exception e) {
                LOGGER.error("删除课程发生异常！异常原因：{}", e);
                return new CommonResponse(CourseCode.DELETE_COURSE_FAIL);
            }
        }

        // 判断为学生用户
        if (user.getRole().getId() == 3) {
            // 确认学生已经加了该门课程
            int i = this.scMapper.selectByStudentIdAndCourseId(userId, courseId);
            if (i != 1) {
                return new CommonResponse(CommonCode.INVALID_PARAM);
            } else {
                // 将选课表关于该学生和该课程的记录删除
                this.scMapper.delete(new QueryWrapper<StudentCourse>().eq("course_id", courseId).eq("student_id", userId));
            }
        }
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    @Transactional
    public CommonResponse joinCourse(Long userId, String code) {
        // 通过加课码查询课程
        Course course = this.courseMapper.selectOne(new QueryWrapper<Course>().eq("c_code", code));
        if (course == null) {
            return new CommonResponse(CourseCode.COURSE_NOT_FOUND);
        }

        // 判断是否重复加课
        int i = this.scMapper.selectByStudentIdAndCourseId(userId, course.getId());
        if (i >= 1) {
            return new CommonResponse(CourseCode.JOIN_COURSE_FAIL);
        }

        // 判断是否为老师加入自己创建的课程
        if (Objects.equals(course.getTeacherId(), userId)) {
            return new CommonResponse(CommonCode.FAIL);
        }

        // 向选课表添加数据
        this.scMapper.joinCourse(userId, course.getId());

        // 更新课程表的加课人数
        course.setSCount(course.getSCount() + 1);
        this.courseMapper.updateById(course);

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    @Transactional
    public CommonResponse saveCourse(Long teacherId, InsertCourse insertCourse) {
        if (insertCourse == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }
        // 校验teacherId是否合法
        User findUser = userMapper.findById(teacherId);
        Role findRole = roleMapper.selectOne(new QueryWrapper<Role>().eq("id", findUser.getRole().getId()));
        if (findRole == null || findRole.getId() != 2) {
            ExceptionThrowUtils.cast(CourseCode.INSERT_COURSE_FAIL);
        }

        Course course = new Course();

        course.setTeacherId(teacherId);
        course.setCName(insertCourse.getName());
        course.setSchoolYear(insertCourse.getSchoolYear());
        course.setSemester(insertCourse.getSemester());
        course.setClazz(insertCourse.getClazz());

        // 生成加课码
        course.setCCode(GenCodeUtils.genRandomCode());

        // set createTime updateTime
        Date date = new Date();
        course.setCreateTime(date);
        course.setUpdateTime(date);
        course.setId(null);

        this.courseMapper.insert(course);
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public QueryResponse findByPage(long currentPage, long pageSize) {
        Page<Course> page = this.courseMapper.selectPage(new Page<>(currentPage, pageSize), null);
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(page.getRecords(), (int) page.getTotal()));
    }

    @Override
    public QueryResponse findByUserId(long userId) {
        //判断用户是否存在
        User findUser = userMapper.findById( userId );
        if(findUser == null){
            ExceptionThrowUtils.cast(UserCode.USER_NOT_FOUND);
        }

        List<StudentCourse> sc = scMapper.selectCourseByUserId(userId);
        return new QueryResponse(CommonCode.SUCCESS,new QueryResult<>(sc,sc.size()));

    }

    @Override
    public QueryResponse findByTeacherId(long teacherId) {
        // 校验teacherId是否合法
        User findUser = userMapper.findById( teacherId );
        Role findRole = roleMapper.selectOne( new QueryWrapper<Role>().eq( "id", findUser.getRole().getId() ) );
        if (findRole == null || findRole.getId() != 2) {
            ExceptionThrowUtils.cast( CourseCode.INSERT_COURSE_FAIL );
        }

        List<Course> courses = this.courseMapper.selectList(new QueryWrapper<Course>().orderByDesc("top_num")
                    .eq("teacher_id",teacherId));
        return new QueryResponse(CommonCode.SUCCESS,new QueryResult<>(courses,courses.size()));
    }

    @Override
    public CommonResponse updateTopSCourse(long courseId, long userId,boolean isTop) {
        //判断课程是否存在
        Course course = courseMapper.selectById(courseId);
        if(course == null){
            ExceptionThrowUtils.cast(CourseCode.COURSE_NOT_FOUND);
        }

        //判断用户是否存在
        User findUser = userMapper.findById( userId );
        if(findUser == null){
            ExceptionThrowUtils.cast(UserCode.USER_NOT_FOUND);
        }

        //置顶
        if(isTop){
            //判断是否已经置顶
            StudentCourse sc = scMapper.selectOne(new QueryWrapper<StudentCourse>().eq("student_id",userId)
                    .eq("course_id",courseId));
            if(sc.getTopNum()>0){
                ExceptionThrowUtils.cast(CourseCode.SET_TOP_FAIL);
            }
            //先查询当前选课置顶课程最大topNum，然后加一设置为新置顶课程的topNum字段
            int currentMaxTop = scMapper.selectMaxTopByStudentId(userId);
            scMapper.updateTopNumByStudentIdAndCourseId(currentMaxTop+1,userId,courseId);
        } else{
            //取消置顶，设置为0
            scMapper.updateTopNumByStudentIdAndCourseId(0,userId,courseId);
        }

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse updateTopTCourse(long courseId, long teacherId,boolean isTop) {
        //判断课程是否存在
        Course course = courseMapper.selectById(courseId);
        if(course == null){
            ExceptionThrowUtils.cast(CourseCode.COURSE_NOT_FOUND);
        }

        // 校验teacherId是否合法
        User findUser = userMapper.findById( teacherId );
        Role findRole = roleMapper.selectOne( new QueryWrapper<Role>().eq( "id", findUser.getRole().getId() ) );
        if (findRole == null || findRole.getId() != 2) {
            ExceptionThrowUtils.cast( CourseCode.INSERT_COURSE_FAIL );
        }

        //置顶
        if (isTop){
            //先查询当前课程topNum，然后加一设置为新置顶课程的topNum字段
            Course findCourse = courseMapper.selectOne(new QueryWrapper<Course>().eq("id",courseId)
                    .eq("teacher_id",teacherId));
            //判断是否已经置顶
            if(findCourse.getTopNum()>0){
                ExceptionThrowUtils.cast(CourseCode.SET_TOP_FAIL);
            }

            findCourse.setTopNum(findCourse.getTopNum()+1);
            courseMapper.updateById(findCourse);

        } else{
            //取消置顶，设置为0
            Course findCourse = courseMapper.selectOne(new QueryWrapper<Course>().eq("id",courseId)
                    .eq("teacher_id",teacherId));
            findCourse.setTopNum(0);
            courseMapper.updateById(findCourse);
        }

        return new CommonResponse(CommonCode.SUCCESS);
    }


}
