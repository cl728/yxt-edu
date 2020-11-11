package com.yixuetang.file.controller;

import com.yixuetang.api.file.FileControllerApi;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传控制层
 * @date 2020/11/11 9:41
 */
@RestController
@RequestMapping("file")
public class FileController implements FileControllerApi {

    @Autowired
    private FileService fileService;

    @Override
    @PostMapping("upload")
    public CommonResponse upload(MultipartFile file) {
        return this.fileService.upload( file );
    }
}
