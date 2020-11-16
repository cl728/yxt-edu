package com.yixuetang.notice.controller;

import com.yixuetang.api.notice.NoticeControllerApi;
import com.yixuetang.entity.request.notice.InsertNotice;
import com.yixuetang.entity.response.CommonResponse;
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
    @PutMapping("courseId/{noticeId}")
    public CommonResponse updateNotice(@PathVariable long noticeId,@RequestBody InsertNotice insertNotice) {
        return this.noticeService.updateNotice(noticeId,insertNotice);
    }

}
