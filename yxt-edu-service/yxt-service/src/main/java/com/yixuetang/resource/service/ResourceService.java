package com.yixuetang.resource.service;

import com.yixuetang.entity.request.resource.InsertResource;
import com.yixuetang.entity.response.CommonResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传服务层接口
 * @date 2020/11/11 9:42
 */
public interface ResourceService {

    /**
     * 文件上传
     *
     * @param file             要上传的文件
     * @param userId           上传者id
     * @param parentResourceId 父级资源id
     * @return 响应结果实体类
     */
    CommonResponse upload(MultipartFile file, Long userId, Long parentResourceId);

    /**
     * 创建文件夹
     *
     * @param userId           创建者id
     * @param parentResourceId 父级资源id
     * @param resource         创建文件夹的资源实体类
     * @return 响应结果实体类
     */
    CommonResponse createFolder(Long userId, Long parentResourceId, InsertResource resource);
}
