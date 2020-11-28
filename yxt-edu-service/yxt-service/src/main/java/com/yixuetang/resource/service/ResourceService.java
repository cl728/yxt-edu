package com.yixuetang.resource.service;

import com.yixuetang.entity.request.resource.DropResource;
import com.yixuetang.entity.request.resource.InsertCourseResource;
import com.yixuetang.entity.request.resource.InsertResource;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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

    /**
     * 根据课程id和父级资源id查询资源列表
     *
     * @param courseId         课程id
     * @param parentResourceId 父级资源id
     * @return 响应结果实体类
     */
    QueryResponse findByCourseIdAndResourceId(Long courseId, Long parentResourceId);

    /**
     * 保存课程-资源记录
     *
     * @param courseResource 课程-资源实体类
     * @return 响应结果实体类
     */
    CommonResponse saveCourseResource(InsertCourseResource courseResource);

    /**
     * 下载资源
     *
     * @param resourceId 资源id
     * @param response   Response
     */
    void download(Long resourceId, HttpServletResponse response);

    /**
     * 根据资源id查找资源实体类
     *
     * @param resourceId 资源id
     * @return 响应结果实体类
     */
    QueryResponse findByResourceId(Long resourceId);

    /**
     * 根据资源id查找其祖宗资源实体列表
     *
     * @param resourceId 资源id
     * @return 响应结果实体类
     */
    QueryResponse findAncestorsByResourceId(Long resourceId);

    /**
     * 教师查询某个学生在某个课程上传的作业
     *
     * @param studentId  学生id
     * @param homeworkId 作业id
     * @return 响应结果实体类
     */
    QueryResponse findHomework(Long studentId, long homeworkId);

    /**
     * 重命名资源
     *
     * @param resourceId 资源id
     * @param name       资源名
     * @return 响应结果实体类
     */
    CommonResponse renameResource(long resourceId, String name);

    /**
     * 删除资源
     *
     * @param resourceId 资源id
     * @param userId     用户id
     * @return 响应结果实体类
     */
    CommonResponse delete(Long resourceId, Long userId);

    /**
     * 查询某一课程下的所有资源
     *
     * @param courseId 课程id
     * @return 响应结果实体类
     */
    QueryResponse findByCourseId(long courseId);

    /**
     * 拖拽某一资源到目标资源旁边或里面
     *
     * @param draggingId   被拖拽的资源id
     * @param dropResource 要拖拽到的目标资源id
     * @return 响应结果实体类
     */
    CommonResponse dropResource(long draggingId, DropResource dropResource);
}
