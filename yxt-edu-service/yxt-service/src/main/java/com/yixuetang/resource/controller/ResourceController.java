package com.yixuetang.resource.controller;

import com.yixuetang.api.resource.ResourceControllerApi;
import com.yixuetang.entity.request.resource.DropResource;
import com.yixuetang.entity.request.resource.InsertCourseResource;
import com.yixuetang.entity.request.resource.InsertResource;
import com.yixuetang.entity.request.resource.UpdateResource;
import com.yixuetang.entity.resource.Resource;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.UploadResponse;
import com.yixuetang.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

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

    @DeleteMapping("resourceId/{resourceId}/userId/{userId}")
    @Override
    public CommonResponse delete(@PathVariable Long resourceId, @PathVariable Long userId) {
        return this.resourceService.delete( resourceId, userId );
    }

    @Override
    @PostMapping("upload/userId/{userId}")
    public CommonResponse upload(MultipartFile file,
                                 @PathVariable Long userId,
                                 @RequestParam(required = false) Long parentResourceId) {
        return this.resourceService.upload( file, userId, parentResourceId );
    }

    @Override
    @PostMapping("upload/editor/userId/{userId}")
    public UploadResponse upload(MultipartFile file, @PathVariable Long userId) {
        QueryResponse queryResponse = (QueryResponse) this.resourceService.upload( file, userId, null );

        @SuppressWarnings("unchecked")
        List<Resource> data = queryResponse.getQueryResult().getData();

        return UploadResponse.builder()
                .errno( queryResponse.isSuccess() ? 0 : 1 )
                .data( Collections.singletonList(
                        CollectionUtils.isEmpty( data )
                                ? null
                                : data.get( 0 ).getLocation()
                ) )
                .build();
    }

    @Override
    @GetMapping("download/resourceId/{resourceId}")
    public void download(@PathVariable Long resourceId, HttpServletResponse response) {
        this.resourceService.download( resourceId, response );
    }

    @Override
    @PostMapping("folder/userId/{userId}")
    public CommonResponse createFolder(@PathVariable Long userId,
                                       @RequestParam(required = false) Long parentResourceId,
                                       @RequestBody InsertResource resource) {
        return this.resourceService.createFolder( userId, parentResourceId, resource );
    }

    @Override
    @GetMapping("courseId/{courseId}")
    public QueryResponse findByCourseIdAndParentResourceId(@PathVariable Long courseId,
                                                           @RequestParam(required = false) Long parentResourceId) {
        return this.resourceService.findByCourseIdAndResourceId( courseId, parentResourceId );
    }

    @Override
    @PostMapping("courseResource")
    public CommonResponse saveCourseResource(@RequestBody InsertCourseResource courseResource) {
        return this.resourceService.saveCourseResource( courseResource );
    }

    @Override
    @GetMapping("resourceId/{resourceId}")
    public QueryResponse findByResourceId(@PathVariable Long resourceId) {
        return this.resourceService.findByResourceId( resourceId );
    }

    @Override
    @GetMapping("ancestors/resourceId/{resourceId}")
    public QueryResponse findAncestorsByResourceId(@PathVariable Long resourceId) {
        return this.resourceService.findAncestorsByResourceId( resourceId );
    }

    @Override
    @GetMapping("homeworkId/{homeworkId}/studentId/{studentId}")
    public QueryResponse findHomework(@PathVariable Long studentId, @PathVariable long homeworkId) {
        return resourceService.findHomework( studentId, homeworkId );
    }

    @Override
    @PutMapping("resourceId/{resourceId}")
    public CommonResponse renameResource(@PathVariable long resourceId,
                                         @RequestBody UpdateResource updateResource) {
        return resourceService.renameResource( resourceId, updateResource.getName() );
    }

    @Override
    @GetMapping("all/courseId/{courseId}")
    public QueryResponse findByCourseId(@PathVariable long courseId) {
        return this.resourceService.findByCourseId( courseId );
    }

    @Override
    @PutMapping("parentResourceId/resourceId/{draggingId}")
    public CommonResponse dropResource(@PathVariable long draggingId,
                                       @RequestBody DropResource dropResource) {
        return this.resourceService.dropResource( draggingId, dropResource );
    }

    @Override
    @GetMapping("page/{currentPage}/{pageSize}")
    public QueryResponse findResourcesByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.resourceService.findResourcesByPage( currentPage, pageSize );
    }
}
