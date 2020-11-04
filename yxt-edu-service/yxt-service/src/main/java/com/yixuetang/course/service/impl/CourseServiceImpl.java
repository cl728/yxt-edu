package com.yixuetang.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.course.service.CourseService;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.course.StudentCourse;
import com.yixuetang.entity.request.course.InsertCourse;
import com.yixuetang.entity.request.course.ListCourse;
import com.yixuetang.entity.request.course.TransferCourse;
import com.yixuetang.entity.request.course.UpdateCourse;
import com.yixuetang.entity.request.user.AvatarUser;
import com.yixuetang.entity.request.user.DelCourseUser;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public CommonResponse deleteCourse(Long courseId, DelCourseUser delCourseUser) {

        // delCourse 不合法
        if (delCourseUser == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        long userId = delCourseUser.getUserId();
        User user = this.userMapper.findById(userId);

        if (user == null) {
            return new CommonResponse( UserCode.USER_NOT_FOUND );
        }

        // 校验密码
        if (!StringUtils.equals( user.getPassword(), delCourseUser.getPassword() )) {
            return new CommonResponse( CourseCode.DELETE_COURSE_FAIL_PASSWORD_WRONG );
        }

        // 判断该id为学生还是老师
        // roleId异常
        if (user.getRole().getId() <= 1 || user.getRole().getId() > 3) {
            return new CommonResponse( CommonCode.INVALID_PARAM);
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
                // 将该课程的加课人数减一
                Course course = this.courseMapper.selectById( courseId );
                course.setSCount( course.getSCount() - 1 );
                this.courseMapper.updateById( course );
            }
        }
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    @Transactional
    public CommonResponse joinCourse(Long userId, String code) {
        // 通过加课码查询课程
        Course course = this.courseMapper.findByCCode(code);
        if (course == null) {
            return new CommonResponse(CourseCode.COURSE_NOT_FOUND);
        }

        // 判断是否重复加课
        int i = this.scMapper.selectByStudentIdAndCourseId(userId, course.getId());
        if (i >= 1) {
            return new CommonResponse(CourseCode.JOIN_COURSE_FAIL);
        }

        // 判断是否为老师加入自己创建的课程
        if (Objects.equals(course.getTeacher().getId(), userId)) {
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
            return new CommonResponse(CourseCode.INSERT_COURSE_FAIL);
        }

        Course course = new Course();

        course.setCName(insertCourse.getName());
        course.setSchoolYear(insertCourse.getSchoolYear());
        course.setSemester(insertCourse.getSemester());
        course.setClazz(insertCourse.getClazz());

        // 生成加课码
        String cCode = GenCodeUtils.genRandomCode();
        course.setCCode(cCode);

        // 设置随机课程图片
        String num = String.valueOf((int) (Math.ceil(Math.random() * 44 )));
        if (num.length() == 1) {
            num = "0" + num;
        }
        course.setCPic("https://assets.ketangpai.com/theme/student/min/" + num + ".png");

        // set createTime updateTime
        Date date = new Date();
        course.setCreateTime(date);
        course.setUpdateTime(date);
        course.setId(null);

        try {
            this.courseMapper.insert(course);

            // 更新课程表的教师id信息
            this.courseMapper.updateTeacherIdByCCode(teacherId, cCode);
        } catch (Exception e) {
            // 加课码设置了唯一索引，如果生成的加课码恰巧一致，则新增课程失败
            LOGGER.error("创建课程发生异常！异常原因：{}", e);
            return new CommonResponse(CommonCode.SERVER_ERROR);
        }

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public QueryResponse findByPage(long currentPage, long pageSize) {
        List<Course> courses = this.courseMapper.findByPage(new Page<>(currentPage, pageSize));
        return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(courses, this.courseMapper.selectCount(null)));
    }

    @Transactional
    @Override
    public CommonResponse transferCourses(Long courseId, Long teacherId, TransferCourse transferCourse) {

        // 1.参数验证
        if (transferCourse == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 根据课程id查询出已有课程信息
        Course course = this.courseMapper.findById(courseId);
        if (course == null) {
            //找不到此课程
            return new CommonResponse(CourseCode.TRANSFER_COURSE_FAIL_COURSE_NOT_FOUND);
        }

        // 3. 根据旧教师id查询教师信息，判断该教师是否为课程授课老师
        User oldTeacher = this.userMapper.findById(teacherId);
        if (ObjectUtils.notEqual(oldTeacher.getId(), course.getTeacher().getId())) {
            //若该教师不是该课程授课老师，则不允许转让
            return new CommonResponse(CourseCode.TRANSFER_COURSE_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER);
        }

        // 4. 比较transferCourse里的旧教师密码,判断是否允许转让
        if (!StringUtils.equals(transferCourse.getPassword(), oldTeacher.getPassword())) {
            //请求转让课程的教师密码错误
            return new CommonResponse(CourseCode.TRANSFER_COURSE_FAIL_PASSWORD_WRONG);
        }

        // 5. 判断新教师的邮箱是否有效
        User newTeacher = this.userMapper.findByEmail(transferCourse.getEmail());
        if (ObjectUtils.isEmpty(newTeacher)) {
            //新教师邮箱无效
            return new CommonResponse(CourseCode.TRANSFER_COURSE_FAIL_EMAIL_NOT_EFFECTIVE);
        }

        // 6. 判断接受转让课程的教师是否与请求转让课程的教师为同一人，是则不允许转让课程
        if (ObjectUtils.equals(newTeacher.getId(), oldTeacher.getId())) {
            return new CommonResponse(CourseCode.TRANSFER_COURSE_FAIL_SAME_TEACHER);
        }

        // 7. 将课程信息中教师id修改为新教师id
        course.setTeacher(newTeacher);
        course.setUpdateTime(new Date()); //更新时间
        this.courseMapper.updateTeacherIdById(course, newTeacher.getId()); //修改授课教师id

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Transactional
    @Override
    public CommonResponse updateCourses(Long courseId, Long teacherId, UpdateCourse updateCourse) {

        // 1.参数验证
        if (updateCourse == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 根据课程id查询出已有课程信息
        Course course = this.courseMapper.findById(courseId);
        if (course == null) {
            //找不到此课程
            return new CommonResponse(CourseCode.UPDATE_COURSE_FAIL_COURSE_NOT_FOUND);
        }

        // 3. 根据旧教师id查询教师信息，判断该教师是否为课程授课老师
        User oldTeacher = this.userMapper.findById(teacherId);
        if (ObjectUtils.notEqual(oldTeacher.getId(), course.getTeacher().getId())) {
            //若该教师不是该课程授课老师，则不允许更信息信息
            return new CommonResponse(CourseCode.UPDATE_COURSE_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER);
        }

        // 4. 更新课程信息
        if (StringUtils.isNoneBlank(updateCourse.getCName())) {
            course.setCName(updateCourse.getCName());
        }
        if (StringUtils.isNoneBlank(updateCourse.getClazz())) {
            course.setClazz(updateCourse.getClazz());
        }
        if (StringUtils.isNoneBlank(updateCourse.getSchoolYear())) {
            course.setSchoolYear(updateCourse.getSchoolYear());
        }
        if (StringUtils.isNoneBlank(updateCourse.getSemester())) {
            course.setSemester(updateCourse.getSemester());
        }
        course.setUpdateTime(new Date()); //更新时间
        this.courseMapper.updateById(course);

        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public CommonResponse file(long courseId, long userId) {
        final User user = this.userMapper.findById(userId);
        Long roleId = user.getRole().getId();
        // roleId非2,3则异常
        if (roleId < 2 || roleId > 3) {
            return new CommonResponse(CommonCode.INVALID_PARAM);
        }

        // 判断为教师用户
        if (roleId == 2) {
            Course course = this.courseMapper.selectOne(new QueryWrapper<Course>()
                    .eq("id", courseId).eq("teacher_id", userId));
            //  若该课程非该教师所有,非法请求
            if (course == null){
                return new CommonResponse(CommonCode.INVALID_PARAM);
            }
            // ifFiled的值,true为已归档,false为未归档
            course.setIsFiled(!course.getIsFiled());
            int i = this.courseMapper.updateById(course);
            if (i != 1) {
                return new CommonResponse(CommonCode.FAIL);
            }
        }

        // 判断为学生用户
        if (roleId == 3) {
            // isFiled设为true
            StudentCourse studentCourse = this.scMapper.selectOne(new QueryWrapper<StudentCourse>().eq("course_id", courseId)
                    .eq("student_id", userId));
            //  若学生没有加入该课程,非法请求
            if (studentCourse == null){
                return new CommonResponse(CommonCode.INVALID_PARAM);
            }
            // ifFiled的值,true为已归档,false为未归档
            studentCourse.setIsFiled(!studentCourse.getIsFiled());
            int i = this.scMapper.updateById(studentCourse);
            if (i != 1) {
                return new CommonResponse(CommonCode.FAIL);
            }
        }
        return new CommonResponse(CommonCode.SUCCESS);
    }

    @Override
    public QueryResponse findCoursesByUserId(long userId) {
        //判断用户是否存在
        User findUser = userMapper.findById(userId);
        if (findUser == null) {
            return new QueryResponse(UserCode.USER_NOT_FOUND, null);
        } else if (findUser.getRole().getId() == 2) {
            //查询教师课程列表
            List<Course> courses = this.courseMapper.selectList(new QueryWrapper<Course>().orderByDesc("top_num")
                    .eq("teacher_id", userId));
            return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(courses, courses.size()));

        } else if (findUser.getRole().getId() == 3) {
            //查询学生加入课程列表
            List<StudentCourse> studentCourseList = scMapper.findByUserId(userId);
            List<ListCourse> listCourses = new ArrayList<>();
            studentCourseList.forEach(item -> {
                ListCourse listCourse = new ListCourse();
                listCourse.setId(item.getCourse().getId());
                listCourse.setCCode(item.getCourse().getCCode());
                listCourse.setClazz(item.getCourse().getClazz());
                listCourse.setCName(item.getCourse().getCName());
                listCourse.setCPic(item.getCourse().getCPic());
                listCourse.setIsFiled(item.getIsFiled());
                listCourse.setSchoolYear(item.getCourse().getSchoolYear());
                listCourse.setSemester(item.getCourse().getSemester());
                listCourse.setTopNum(item.getTopNum());
                listCourse.setAvatarUser(new AvatarUser());
                // avatarUser 修改为教师的头像和真实姓名
                User user = this.userMapper.findByCourseId( item.getCourse().getId() );
                listCourse.getAvatarUser().setAvatar(user.getAvatar());
                listCourse.getAvatarUser().setRealName(user.getRealName());
                listCourses.add(listCourse);
            });
            return new QueryResponse(CommonCode.SUCCESS, new QueryResult<>(listCourses, listCourses.size()));
        } else {
            return new QueryResponse(CommonCode.FAIL, null);
        }
    }


    @Override
    public CommonResponse updateTopCourse(long courseId, long userId) {
        //判断课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return new CommonResponse(CourseCode.COURSE_NOT_FOUND);
        }
        //判断用户是否存在
        User findUser = userMapper.findById(userId);
        if (findUser == null) {
            return new CommonResponse(UserCode.USER_NOT_FOUND);
        } else if (findUser.getRole().getId() == 2) {
            //置顶教师课程
            //先查询当前课程topNum，然后加一设置为新置顶课程的topNum字段
            Course findCourse = courseMapper.selectOne(new QueryWrapper<Course>().eq("id", courseId)
                    .eq("teacher_id", userId));
            //判断是否已经置顶
            if (findCourse.getTopNum() > 0) {
                //已经置顶则取消置顶
                findCourse.setTopNum(0);
                courseMapper.updateById(findCourse);
            } else {
                //置顶
                findCourse.setTopNum(10);
                courseMapper.updateById(findCourse);
            }

        } else if (findUser.getRole().getId() == 3) {
            //置顶学生课程
            StudentCourse sc = scMapper.selectOne(new QueryWrapper<StudentCourse>().eq("course_id", courseId)
                    .eq("student_id", userId));
            //判断是否已经置顶
            if (sc.getTopNum() > 0) {
                //已经置顶则取消置顶
                sc.setTopNum(0);
                scMapper.updateById(sc);
            } else {
                //置顶
                sc.setTopNum(10);
                scMapper.updateById(sc);
            }
        } else {
            return new CommonResponse(CommonCode.FAIL);
        }

        return new CommonResponse(CommonCode.SUCCESS);
    }


}
