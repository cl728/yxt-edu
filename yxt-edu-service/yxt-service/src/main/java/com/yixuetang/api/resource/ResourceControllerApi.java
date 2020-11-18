package com.yixuetang.api.resource;

import com.yixuetang.entity.request.resource.InsertCourseResource;
import com.yixuetang.entity.request.resource.InsertResource;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传接口
 * @date 2020/11/11 9:33
 */
@Api(value = "资源模块接口", description = "资源模块接口，提供资源的上传、下载")
public interface ResourceControllerApi {

    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "parentResourceId", value = "父级资源id",
                    paramType = "query", dataType = "long")
    })
    CommonResponse upload(MultipartFile file, Long userId, Long parentResourceId);

    @ApiOperation("下载文件")
    @ApiImplicitParam(name = "resourceId", value = "资源id", required = true,
            paramType = "path", dataType = "long")
    void download(Long resourceId, HttpServletResponse response);

    @ApiOperation("创建文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "parentResourceId", value = "父级资源id",
                    paramType = "query", dataType = "long")
    })
    CommonResponse createFolder(Long userId, Long parentResourceId, InsertResource resource);

    @ApiOperation("根据课程id和父级资源id查询资源列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true,
                    paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "parentResourceId", value = "父级资源id",
                    paramType = "query", dataType = "long")
    })
    QueryResponse findByCourseIdAndParentResourceId(Long courseId, Long parentResourceId);

    @ApiOperation("保存课程-资源记录")
    CommonResponse saveCourseResource(InsertCourseResource courseResource);

    @ApiOperation("根据资源id查找资源实体类")
    @ApiImplicitParam(name = "resourceId", value = "资源id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findByResourceId(Long resourceId);

    @ApiOperation("根据资源id查找其祖宗资源实体列表")
    @ApiImplicitParam(name = "resourceId", value = "资源id", required = true,
            paramType = "path", dataType = "long")
    QueryResponse findAncestorsByResourceId(Long resourceId);
}
