package com.yixuetang.api.resource;

import com.yixuetang.entity.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传接口
 * @date 2020/11/11 9:33
 */
@Api(value = "资源模块接口", description = "资源模块接口，提供图片、视频等资源的上传、下载")
public interface ResourceControllerApi {

    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "parentResourceId", value = "父级资源id",
                    paramType = "query", dataType = "long")
    })
    CommonResponse upload(MultipartFile file, Long userId, Long parentResourceId);

}