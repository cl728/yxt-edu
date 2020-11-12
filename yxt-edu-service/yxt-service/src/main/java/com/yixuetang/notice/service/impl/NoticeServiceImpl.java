package com.yixuetang.notice.service.impl;

import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.notice.Notice;
import com.yixuetang.entity.notice.NoticeUser;
import com.yixuetang.entity.request.notice.InsertNotice;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.notice.NoticeCode;
import com.yixuetang.entity.user.User;
import com.yixuetang.notice.mapper.NoticeMapper;
import com.yixuetang.notice.mapper.NoticeUserMapper;
import com.yixuetang.notice.service.NoticeService;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Curits
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/12 12:26
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScMapper scMapper;

    @Autowired
    private NoticeUserMapper noticeUserMapper;

    @Transactional
    @Override
    public CommonResponse saveNotice(long courseId, long teacherId, InsertNotice insertNotice) {

        // 1. courseId 不合法
        Course course = this.courseMapper.findById(courseId);
        if (course == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 2. 参数验证
        if (insertNotice == null) {
            ExceptionThrowUtils.cast(CommonCode.INVALID_PARAM);
        }

        // 3. 判断当前用户是否为该课程教师
        User oldTeacher = this.userMapper.findById(teacherId);
        if (ObjectUtils.notEqual(oldTeacher.getId(), course.getTeacher().getId())) {
            //若该教师不是该课程授课老师，则不允许新增公告
            return new CommonResponse(NoticeCode.INSERT_NOTICE_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER);
        }

        // 4. 公告标题非空判断
        if (!StringUtils.isNoneBlank(insertNotice.getTitle())) {
            return new CommonResponse(NoticeCode.INSERT_NOTICE_FAIL_TITLE_IS_NULL);
        }

        // 0. 公告内容非空判断
        if (!StringUtils.isNoneBlank(insertNotice.getContent())) {
            return new CommonResponse(NoticeCode.INSERT_NOTICE_FAIL_CONTENT_IS_NULL);
        }

        // 0. 根据课程id创建公告
        Notice notice = new Notice();
        notice.setTitle(insertNotice.getTitle());
        notice.setContent(insertNotice.getContent());
        notice.setCourse(course);
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        this.noticeMapper.insertNotice(notice);

        // 0. 根据课程id查询选修了该课程的所有学生
        List<Long> studentIds = this.scMapper.findStudentIdByCourseId(courseId);

        // 0. 根据刚创建的公告id新建公告与用户表的记录
        //教师记录
        NoticeUser noticeUserT = new NoticeUser();
        noticeUserT.setUserId(oldTeacher.getId());
        noticeUserT.setNoticeId(notice.getId());
        noticeUserT.setView(false);
        this.noticeUserMapper.insert(noticeUserT);
        //学生记录
        for (Long studentId : studentIds) {
            NoticeUser noticeUserS = new NoticeUser();
            noticeUserS.setNoticeId(notice.getId());
            noticeUserS.setUserId(studentId);
            noticeUserS.setView(false);
            this.noticeUserMapper.insert(noticeUserS);
        }

        return new CommonResponse(CommonCode.SUCCESS);
    }
}
