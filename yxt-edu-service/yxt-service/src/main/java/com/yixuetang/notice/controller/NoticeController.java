package com.yixuetang.notice.controller;

import com.yixuetang.api.notice.NoticeControllerApi;
import com.yixuetang.entity.request.notice.InsertNotice;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Curtis
 * @version 1.0.0
 * @description 课程模块下的公告控制层
 * @date 2020/11/12 12:24
 */
@RestController
@RequestMapping("notice")
public class NoticeController implements NoticeControllerApi {

    @Autowired
    private NoticeService noticeService;

    @Override
    @GetMapping("courseId/{courseId}")
    public QueryResponse findNotices(@PathVariable long courseId) {
        return this.noticeService.findNotices(courseId);
    }

    @Override
    @PostMapping("courseId/{courseId}/{teacherId}")
    public CommonResponse saveNotice(@PathVariable long courseId, @PathVariable long teacherId, @RequestBody InsertNotice insertNotice) {
        return this.noticeService.saveNotice(courseId, teacherId, insertNotice);
    }

    @Override
    @DeleteMapping("courseId/{noticeId}")
    public CommonResponse deleteNotice(@PathVariable long noticeId) {
        return this.noticeService.deleteNotice(noticeId);
    }

    @Override
    @PutMapping("info/noticeId/{noticeId}")
    public CommonResponse updateNotice(@PathVariable long noticeId, @RequestBody InsertNotice insertNotice) {
        return this.noticeService.updateNotice(noticeId, insertNotice);
    }

    @Override
    @GetMapping("noticeId/{noticeId}")
    public QueryResponse findById(@PathVariable long noticeId) {
        return this.noticeService.findById(noticeId);
    }

    @Override
    @PutMapping("topNum/noticeId/{noticeId}")
    public CommonResponse switchTop(@PathVariable long noticeId) {
        return this.noticeService.switchTopNum(noticeId);
    }

    @Override
    @PutMapping("noticeId/{noticeId}/userId/{userId}")
    public CommonResponse updateReadStatus(@PathVariable long noticeId, @PathVariable long userId) {
        return this.noticeService.updateReadStatus(noticeId, userId);
    }

}
