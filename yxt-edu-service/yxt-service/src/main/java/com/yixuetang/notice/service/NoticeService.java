package com.yixuetang.notice.service;

import com.yixuetang.entity.request.notice.InsertNotice;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;

/**
 * @author Curtis
 * @version 1.0.0
 * @description TODO
 * @date 2020/11/12 12:25
 */
public interface NoticeService {

    /**
     * 根据课程id新增公告
     *
     * @param courseId     课程id
     * @param teacherId    教师id
     * @param insertNotice 新增公告实体类
     * @return 通用响应实体类
     */
    CommonResponse saveNotice(long courseId, long teacherId, InsertNotice insertNotice);

    /**
     * 教师删除公告
     *
     * @param noticeId 公告id
     * @return 通用响应实体类
     */
    CommonResponse deleteNotice(long noticeId);

    /**
     * 修改公告
     *
     * @param noticeId     公告id
     * @param insertNotice 修改公告实体类
     * @return 通用响应实体类
     */
    CommonResponse updateNotice(long noticeId, InsertNotice insertNotice);

    /**
     * 根据课程id查询公告列表
     *
     * @param courseId 课程id
     * @return 带查询结果集的响应实体类
     */
    QueryResponse findNotices(long courseId);

    /**
     * 根据公告id查询公告信息
     *
     * @param noticeId 课程公告Id
     * @return 带查询结果集的响应实体类
     */
    QueryResponse findById(long noticeId);

    /**
     * 根据公告id切换置顶字段
     *
     * @param noticeId 课程公告Id
     * @return 通用响应实体类
     */
    CommonResponse switchTopNum(long noticeId);
}
