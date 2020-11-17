package com.yixuetang.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.request.resource.InsertResource;
import com.yixuetang.entity.resource.Resource;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.resource.ResourceCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.resource.mapper.ResourceMapper;
import com.yixuetang.resource.service.ResourceService;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import com.yixuetang.utils.resource.ResourceUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传服务层接口实现类
 * @date 2020/11/11 9:44
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceUtils resourceUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private CourseMapper courseMapper;

    // 支持的文件类型：.jpg .jpeg .png .mp4 .avi .doc .xls .pdf .ppt .txt
    private static final List<String> CONTENT_TYPES = Arrays.asList( "image/jpeg", "image/png", "video/mpeg4",
            "video/avi", "application/msword", "application/x-xls", "application/pdf", "application/x-ppt", "text/plain" );

    private static final Logger LOGGER = LoggerFactory.getLogger( ResourceServiceImpl.class );

    @Override
    @Transactional
    public CommonResponse upload(MultipartFile file, Long userId, Long parentResourceId) {

        // 用户id不合法
        if (this.userMapper.selectOne( new QueryWrapper<User>().eq( "id", userId ).select( "id" ) ) == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        if (file == null) {
            return new CommonResponse( CommonCode.INVALID_PARAM );
        }

        String originalFilename = file.getOriginalFilename();
        // 校验文件的类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains( contentType )) {
            // 文件类型不合法
            LOGGER.info( "文件类型不合法：{}", originalFilename );
            return new CommonResponse( ResourceCode.CONTENT_TYPE_INVALID );
        }

        // 保存到服务器
        String filePath = this.resourceUtils.upload( file );

        if (StringUtils.isBlank( filePath )) {
            return new CommonResponse( CommonCode.FAIL );
        }

        String ext = StringUtils.substringAfterLast( originalFilename, "." );
        Resource resource = Resource.builder()
                .id( null )
                .type( 1 )
                .ext( ext )
                .name( originalFilename )
                .location( filePath )
                .createTime( new Date() )
                .updateTime( new Date() ).build();
        // 保存到资源表中
        this.saveToDatabase( userId, parentResourceId, resource );

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( Collections.singletonList( filePath ), 1 ) );

    }

    @Override
    @Transactional
    public CommonResponse createFolder(Long userId, Long parentResourceId, InsertResource resource) {

        // userId 或者 resource 不合法
        if (resource == null || this.userMapper.selectOne( new QueryWrapper<User>().eq( "id", userId ).select( "id" ) ) == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        Resource insertResource = Resource.builder()
                .id( null )
                .type( 0 )
                .ext( null )
                .name( resource.getName() )
                .location( null )
                .createTime( new Date() )
                .updateTime( new Date() ).build();
        // 保存到资源表中
        this.saveToDatabase( userId, parentResourceId, insertResource );

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( Collections.singletonList( insertResource ), 1 ) );
    }

    @Override
    public QueryResponse findByCourseIdAndResourceId(Long courseId, Long parentResourceId) {

        // courseId 或者 parentResourceId 不合法
        if (this.courseMapper.selectOne( new QueryWrapper<Course>().eq( "id", courseId ).select( "id" ) ) == null ||
                (ObjectUtils.isNotEmpty( parentResourceId )
                        && ObjectUtils.notEqual( -1L, parentResourceId )
                        && this.resourceMapper.selectOne(
                        new QueryWrapper<Resource>().eq( "id", parentResourceId ).select( "id" ) ) == null)) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        if (ObjectUtils.isEmpty( parentResourceId )) {
            parentResourceId = -1L;
        }

        List<Resource> resourceList = this.resourceMapper.findByCourseIdAndParentId( courseId, parentResourceId );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( resourceList, resourceList.size() ) );
    }

    private void saveToDatabase(Long userId, Long parentResourceId, Resource resource) {

        this.resourceMapper.insert( resource );

        // 关联userId
        this.resourceMapper.updateUserIdById( userId, resource.getId() );

        // 关联parentResourceId
        if (ObjectUtils.isNotEmpty( parentResourceId )) {
            if (this.resourceMapper.selectOne( new QueryWrapper<Resource>().eq( "id", parentResourceId ).select( "id" ) ) == null) {
                ExceptionThrowUtils.cast( ResourceCode.PARENT_RESOURCE_NOT_EXISTS );
            } else {
                this.resourceMapper.updateParentResourceIdById( parentResourceId, resource.getId() );
            }
        }

    }
}
