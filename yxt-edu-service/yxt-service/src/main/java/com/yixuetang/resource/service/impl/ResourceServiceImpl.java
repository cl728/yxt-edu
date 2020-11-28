package com.yixuetang.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.homework.Homework;
import com.yixuetang.entity.homework.HomeworkResource;
import com.yixuetang.entity.request.resource.InsertCourseResource;
import com.yixuetang.entity.request.resource.InsertResource;
import com.yixuetang.entity.resource.CourseResource;
import com.yixuetang.entity.resource.Resource;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.resource.ResourceCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.User;
import com.yixuetang.homework.mapper.HomeworkMapper;
import com.yixuetang.homework.mapper.HomeworkResourceMapper;
import com.yixuetang.mq.AmqpUtils;
import com.yixuetang.resource.mapper.CourseResourceMapper;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private CourseResourceMapper courseResourceMapper;

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private HomeworkResourceMapper homeworkResourceMapper;

    @javax.annotation.Resource(name = "template")
    private RedisTemplate<String, Object> template;

    @Autowired
    private AmqpUtils amqpUtils;

    private final String ANCESTORS_KEY_PREFIX = "resource:ancestors:id:";

    // 支持的文件类型：.jpg .jpeg .png .mp4 .avi .doc .xls .pdf .ppt .pptx .xlsx .txt
    private static final List<String> CONTENT_TYPES = Arrays.asList( "image/jpeg", "image/png", "video/mpeg4", "video/mp4",
            "video/avi", "application/msword", "application/x-xls", "application/pdf", "application/x-ppt", "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", "text/plain",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/x-zip-compressed" );

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
                .contentType( contentType )
                .name( originalFilename )
                .location( filePath )
                .createTime( new Date() )
                .updateTime( new Date() ).build();
        // 保存到资源表中
        this.saveToDatabase( userId, parentResourceId, resource );

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( Collections.singletonList( resource ), 1 ) );

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
                .contentType( null )
                .name( resource.getName() )
                .location( null )
                .createTime( new Date() )
                .updateTime( new Date() ).build();
        // 保存到资源表中
        this.saveToDatabase( userId, parentResourceId, insertResource );
        // 保存到课程-资源记录表中
        this.courseResourceMapper.insert( CourseResource.builder().id( null ).courseId( resource.getCourseId() ).resourceId( insertResource.getId() ).build() );

        return CommonResponse.SUCCESS();
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

    @Override
    @Transactional
    public CommonResponse saveCourseResource(InsertCourseResource courseResource) {
        this.courseResourceMapper.insert( CourseResource.builder().id( null ).courseId( courseResource.getCourseId() ).resourceId( courseResource.getResourceId() ).build() );

        // 发送异步事件提醒，告知学生教师发布了新资源
        this.amqpUtils.sendCourseRemind( courseResource.getTeacherId(), courseResource.getCourseId(),
                courseResource.getResourceId(), "资源", "发布",
                "http://www.yixuetang.com/courseDetail.html?id=" + courseResource.getCourseId() );

        return CommonResponse.SUCCESS();
    }

    @Override
    public void download(Long resourceId, HttpServletResponse response) {
        Resource resource = this.resourceMapper.selectById( resourceId );
        if (resource == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        resourceUtils.download( response, resource );
    }

    @Override
    public QueryResponse findByResourceId(Long resourceId) {
        Resource resource = this.resourceMapper.selectOne( new QueryWrapper<Resource>().eq( "id", resourceId ).select( "name", "location" ) );
        if (resource == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( Collections.singletonList( resource ), 1 ) );
    }

    @Override
    public QueryResponse findAncestorsByResourceId(Long resourceId) {

        List<Resource> ancestors = new ArrayList<>();

        // 先从 redis 查询
        Set<ZSetOperations.TypedTuple<Object>> typedTuples =
                template.opsForZSet()
                        .reverseRangeWithScores( ANCESTORS_KEY_PREFIX + resourceId, 0, -1 );
        if (CollectionUtils.isEmpty( typedTuples )) { // redis 没有记录，则从数据库查询

            Resource resource = this.resourceMapper.findAncestorsByResourceId( resourceId );

            while (resource != null && resource.getParentResource() != null) {
                ancestors.add( resource );
                resource = this.resourceMapper
                        .findAncestorsByResourceId( resource.getParentResource().getId() );
            }

            ancestors.add( resource );

            // 将查询到的结果存入 redis
            ancestors.forEach(
                    resource1 ->
                            this.template.opsForZSet()
                                    .add( ANCESTORS_KEY_PREFIX + resourceId,
                                            resource1.getName(), resource1.getId().doubleValue() ) );

            // 设置过期时间 - 10天
            this.template.expire( ANCESTORS_KEY_PREFIX + resourceId, 10L, TimeUnit.DAYS );

        } else { // redis 有记录，将其结果取出，并放置到 ancestors 中

            typedTuples.forEach(
                    tuple -> ancestors.add(
                            Resource.builder()
                                    .id( Objects.requireNonNull( tuple.getScore() ).longValue() )
                                    .name( (String) tuple.getValue() )
                                    .build() )
            );
        }

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( ancestors, ancestors.size() ) );
    }

    @Override
    public QueryResponse findHomework(Long studentId, long homeworkId) {
        // 学生id不合法
        User user = userMapper.findById( studentId );
        if (user == null || user.getRole().getId() != 3) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        // 作业id不合法
        if (this.homeworkMapper.selectOne( new QueryWrapper<Homework>().eq( "id", homeworkId ).select( "id" ) ) == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        //查询学生提交的作业-资源记录 是否为空
        List<HomeworkResource> homeworkResources = homeworkResourceMapper.selectList( new QueryWrapper<HomeworkResource>().eq( "student_id", studentId ).eq( "homework_id", homeworkId ) );
        if (homeworkResources.size() <= 0) {
            return new QueryResponse( ResourceCode.HOMEWORK_RESOURCE_NOT_EXISTS, null );
        }

        //获取资源id
        List<Long> resourceIds = new ArrayList<>();
        for (HomeworkResource homeworkResource : homeworkResources) {
            resourceIds.add( homeworkResource.getResourceId() );
        }

        //根据学生作业-资源记录的资源id查询资源
        List<Resource> resources = resourceMapper.selectList( new QueryWrapper<Resource>().in( "id", resourceIds ) );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult( resources, resources.size() ) );
    }

    @Override
    public CommonResponse renameResource(long resourceId, String name) {
        //  判断资源是否存在
        Resource resource = resourceMapper.selectById( resourceId );
        if (resource == null) {
            ExceptionThrowUtils.cast( ResourceCode.RESOURCE_NOT_EXISTS );
        }
        //  判断输入资源名称是否为空
        if (!StringUtils.isNoneBlank( name )) {
            ExceptionThrowUtils.cast( ResourceCode.RESOURCE_NAME_CAN_NOT_BE_EMPTY );
        }
        resource.setName( name );
        resource.setUpdateTime( new Date() );
        resourceMapper.updateById( resource );
        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Transactional
    @Override
    public CommonResponse delete(Long resourceId, Long userId) {

        // 1. 判断资源是否存在
        Resource resource = this.resourceMapper.findById( resourceId );
        if (resource == null) {
            ExceptionThrowUtils.cast( ResourceCode.RESOURCE_NOT_EXISTS );
        }

        // 2. 判断当前用户是否为资源上传者或管理员
        if (ObjectUtils.notEqual( userId, resource.getUser().getId() ) &&
                ObjectUtils.notEqual( 1L, this.userMapper.findById( userId ).getRole().getId() )) {
            return new CommonResponse( ResourceCode.USER_IS_NOT_RESOURCE_OWNER_OR_ADMIN );
        }

        // 3. 删除文件
        // 3.1 判断资源是否是文件夹
        if (resource.getType() == 1) {
            // 3.2 如果不是，直接删除
            this.delFromDatabase( resource );
        } else {
            // 3.3 如果是，递归找出其子级资源
            this.forEachSetChildList( Collections.singletonList( resource ) );
            // 3.4 再递归地将它们删除
            this.delRecursively( resource );
        }
        return CommonResponse.SUCCESS();
    }

    @Override
    public QueryResponse findByCourseId(long courseId) {

        if (ObjectUtils.isEmpty( this.courseMapper.selectById( courseId ) )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        List<Resource> topResourceList = this.resourceMapper.findByCourseIdAndParentId( courseId, -1L );

        this.forEachSetChildList( topResourceList );

        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( topResourceList, topResourceList.size() ) );
    }

    private void saveToDatabase(Long userId, Long parentResourceId, Resource resource) {

        this.resourceMapper.insert( resource );

        // 关联userId
        this.resourceMapper.updateUserIdById( userId, resource.getId() );

        // 关联parentResourceId
        if (ObjectUtils.isNotEmpty( parentResourceId ) && ObjectUtils.notEqual( -1L, parentResourceId )) {
            if (this.resourceMapper.selectOne( new QueryWrapper<Resource>().eq( "id", parentResourceId ).select( "id" ) ) == null) {
                ExceptionThrowUtils.cast( ResourceCode.PARENT_RESOURCE_NOT_EXISTS );
            } else {
                this.resourceMapper.updateParentResourceIdById( parentResourceId, resource.getId() );
            }
        }

    }

    private void forEachSetChildList(List<Resource> resourceList) {
        if (!CollectionUtils.isEmpty( resourceList )) {
            resourceList.forEach( resource -> {
                if (resource.getType() == 0) { // 如果是目录，查询其子级资源（可能为空）
                    resource.setChildResourceList( this.resourceMapper.findChildResourceListByResourceId( resource.getId() ) );
                    this.forEachSetChildList( resource.getChildResourceList() );
                }
            } );
        }
    }

    private void delRecursively(Resource resource) {
        List<Resource> childResourceList = resource.getChildResourceList();
        if (!CollectionUtils.isEmpty( childResourceList )) {
            // 先删除子级资源
            childResourceList.forEach( this::delRecursively );
        }
        this.delFromDatabase( resource );
    }

    private void delFromDatabase(Resource resource) {
        // 1. 删除服务器上的文件
        if (resource.getType() == 1) {
            boolean deleteFlag = this.resourceUtils.delete( resource.getLocation() );
            if (!deleteFlag) {
                ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
            }
        }
        // 2. 删除t_course_resource表的记录
        this.courseResourceMapper.delete( new QueryWrapper<CourseResource>().eq( "resource_id", resource.getId() ) );
        // 3. 删除t_resource表的记录
        this.resourceMapper.deleteById( resource.getId() );
    }
}
