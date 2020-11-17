package com.yixuetang.resource.controller;

import com.yixuetang.api.resource.ResourceControllerApi;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传控制层
 * @date 2020/11/11 9:41
 */
@RestController
@RequestMapping("resource")
public class ResourceController implements ResourceControllerApi {

    @Autowired
    private ResourceService resourceService;

    @Override
    @PostMapping("upload/userId/{userId}")
    public CommonResponse upload(MultipartFile file,
                                 @PathVariable Long userId,
                                 @RequestParam(required = false) Long parentResourceId) {
        return this.resourceService.upload( file, userId, parentResourceId );
    }
}