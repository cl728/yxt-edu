package com.yixuetang.api.file;

import com.yixuetang.entity.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传接口
 * @date 2020/11/11 9:33
 */
@Api(value = "文件上传接口", description = "文件上传接口，提供图片、视频等文件的上传")
public interface FileControllerApi {

    @ApiOperation("上传文件")
    CommonResponse upload(MultipartFile file);

}
