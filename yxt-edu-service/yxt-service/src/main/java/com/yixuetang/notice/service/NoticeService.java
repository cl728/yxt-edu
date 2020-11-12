package com.yixuetang.notice.service;

import com.yixuetang.entity.request.notice.InsertNotice;
import com.yixuetang.entity.response.CommonResponse;

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
     * @return 通用相应实体类
     */
    CommonResponse saveNotice(long courseId, long teacherId, InsertNotice insertNotice);
}
