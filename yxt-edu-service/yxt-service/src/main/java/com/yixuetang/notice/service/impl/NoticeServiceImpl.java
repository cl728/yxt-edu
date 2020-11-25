package com.yixuetang.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.notice.Notice;
import com.yixuetang.entity.notice.NoticeUser;
import com.yixuetang.entity.request.notice.InsertNotice;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.notice.NoticeCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.mq.AmqpUtils;
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

import java.util.Collections;
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

    @Autowired
    private AmqpUtils amqpUtils;

    @Transactional
    @Override
    public CommonResponse saveNotice(long courseId, long teacherId, InsertNotice insertNotice) {

        // 1. courseId 不合法
        Course course = this.courseMapper.findById( courseId );
        if (course == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 参数验证
        if (insertNotice == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 3. 判断当前用户是否为该课程教师
        User oldTeacher = this.userMapper.findById( teacherId );
        if (ObjectUtils.notEqual( oldTeacher.getId(), course.getTeacher().getId() )) {
            //若该教师不是该课程授课老师，则不允许新增公告
            return new CommonResponse( NoticeCode.INSERT_NOTICE_FAIL_COURSE_NOT_BELONGS_TO_THIS_TEACHER );
        }

        // 4. 公告标题非空判断
        if (!StringUtils.isNoneBlank( insertNotice.getTitle() )) {
            return new CommonResponse( NoticeCode.INSERT_NOTICE_FAIL_TITLE_IS_NULL );
        }

        // 0. 公告内容非空判断
        if (!StringUtils.isNoneBlank( insertNotice.getContent() )) {
            return new CommonResponse( NoticeCode.INSERT_NOTICE_FAIL_CONTENT_IS_NULL );
        }

        // 0. 根据课程id创建公告
        Notice notice = new Notice();
        notice.setTitle( insertNotice.getTitle() );
        notice.setContent( insertNotice.getContent() );
        notice.setCourse( course );
        notice.setCreateTime( new Date() );
        notice.setUpdateTime( new Date() );
        notice.setTopNum( 0 );
        this.noticeMapper.insertNotice( notice );

        // 0. 根据课程id查询选修了该课程的所有学生
        List<Long> studentIds = this.scMapper.findStudentIdByCourseId( courseId );

        // 0. 根据刚创建的公告id新建公告与用户表的记录
        //教师记录
        NoticeUser noticeUserT = new NoticeUser();
        noticeUserT.setUserId( oldTeacher.getId() );
        noticeUserT.setNoticeId( notice.getId() );
        noticeUserT.setView( false );
        this.noticeUserMapper.insert( noticeUserT );
        //学生记录
        for (Long studentId : studentIds) {
            NoticeUser noticeUserS = new NoticeUser();
            noticeUserS.setNoticeId( notice.getId() );
            noticeUserS.setUserId( studentId );
            noticeUserS.setView( false );
            this.noticeUserMapper.insert( noticeUserS );
        }

        // 发送异步事件提醒，告知学生教师发布了新公告
        this.amqpUtils.sendCourseRemind( teacherId, courseId, notice.getId(), "公告", "发布",
                "http://www.yixuetang.com/courseDetail.html?id=" + courseId );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @Transactional
    public CommonResponse deleteNotice(long noticeId) {
        // 1. 公告是否存在
        Notice notice = noticeMapper.selectOne( new QueryWrapper<Notice>().eq( "id", noticeId ) );
        if (notice == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2.删除公告与用户表的记录
        noticeUserMapper.delete( new QueryWrapper<NoticeUser>().eq( "notice_id", noticeId ) );

        // 3.删除公告
        noticeMapper.delete( new QueryWrapper<Notice>().eq( "id", noticeId ) );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Transactional
    @Override
    public CommonResponse updateNotice(long noticeId, InsertNotice insertNotice) {
        //  公告是否存在
        Notice notice = noticeMapper.selectOne( new QueryWrapper<Notice>().eq( "id", noticeId ) );
        if (notice == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 参数验证
        if (insertNotice == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 4. 公告标题非空判断
        if (!StringUtils.isNoneBlank( insertNotice.getTitle() )) {
            return new CommonResponse( NoticeCode.INSERT_NOTICE_FAIL_TITLE_IS_NULL );
        }

        // 0. 公告内容非空判断
        if (!StringUtils.isNoneBlank( insertNotice.getContent() )) {
            return new CommonResponse( NoticeCode.INSERT_NOTICE_FAIL_CONTENT_IS_NULL );
        }

        //修改公告
        notice.setContent( insertNotice.getContent() );  //修改内容
        notice.setTitle( insertNotice.getTitle() );      //修改标题
        notice.setUpdateTime( new Date() );              //修改更新时间
        noticeMapper.updateById( notice );

        //修改公告之后，公告和用户之间的记录设置为未读
        List<NoticeUser> noticeUsers = noticeUserMapper.selectList( new QueryWrapper<NoticeUser>().eq( "notice_id", notice.getId() ) );
        for (NoticeUser noticeUser : noticeUsers) {
            noticeUser.setView( false );
            noticeUserMapper.updateById( noticeUser );
        }

        return new CommonResponse( CommonCode.SUCCESS );
    }

    /**
     * 根据课程id查询该门课程下的公告列表
     *
     * @param courseId 课程id
     * @return 带查询结果集的响应实体类
     */
    @Override
    public QueryResponse findNotices(long courseId) {

        // 1. 判断courseId 不合法
        Course course = this.courseMapper.findById( courseId );
        if (course == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 0. 根据课程id查询该门课程下的公告列表
        List<Notice> notices = this.noticeMapper.findNoticeByCourseId( courseId );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( notices, notices.size() ) );
    }

    @Override
    public QueryResponse findById(long noticeId) {
        //  公告是否存在
        Notice notice = noticeMapper.findById( noticeId );
        if (notice == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( Collections.singletonList( notice ), 1 ) );
    }

    @Override
    @Transactional
    public CommonResponse switchTopNum(long noticeId) {
        //  公告是否存在
        Notice notice = noticeMapper.selectOne( new QueryWrapper<Notice>().eq( "id", noticeId ) );
        if (notice == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        notice.setTopNum( notice.getTopNum() == 0 ? 10 : 0 );
        notice.setUpdateTime( new Date() );
        this.noticeMapper.updateById( notice );
        return CommonResponse.SUCCESS();
    }

    @Override
    public CommonResponse updateReadStatus(long noticeId, long userId) {
        Notice notice = noticeMapper.selectOne( new QueryWrapper<Notice>().eq( "id", noticeId ) );
        NoticeUser noticeUser = this.noticeUserMapper.selectOne( new QueryWrapper<NoticeUser>().eq( "notice_id", noticeId ).eq( "user_id", userId ) );
        //  公告是否存在
        if (notice == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        //  判断公告view字段是否已改, 若为false则改为true
        if (!noticeUser.getView()) {
            noticeUser.setView( !noticeUser.getView() );
            noticeUserMapper.updateById( noticeUser );
        }
        return CommonResponse.SUCCESS();
    }

}
