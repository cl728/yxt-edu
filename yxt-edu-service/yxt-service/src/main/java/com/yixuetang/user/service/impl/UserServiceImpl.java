package com.yixuetang.user.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.course.mapper.CourseMapper;
import com.yixuetang.course.mapper.ScMapper;
import com.yixuetang.entity.auth.UserInfo;
import com.yixuetang.entity.course.Course;
import com.yixuetang.entity.course.StudentCourse;
import com.yixuetang.entity.homework.Homework;
import com.yixuetang.entity.homework.HomeworkStudent;
import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.request.user.*;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.user.UserCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.response.result.UserResp;
import com.yixuetang.entity.user.Role;
import com.yixuetang.entity.user.School;
import com.yixuetang.entity.user.User;
import com.yixuetang.homework.mapper.HomeworkMapper;
import com.yixuetang.homework.mapper.HomeworkStudentMapper;
import com.yixuetang.user.mapper.RoleMapper;
import com.yixuetang.user.mapper.SchoolMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.user.service.UserService;
import com.yixuetang.utils.auth.CookieUtils;
import com.yixuetang.utils.auth.JwtConfig;
import com.yixuetang.utils.auth.JwtUtils;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import com.yixuetang.utils.user.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块服务层接口实现类
 * @date 2020/10/23 16:09
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger( UserServiceImpl.class );
    private static final String LOGIN_KEY_PREFIX = "user:code:login:";
    private static final String REGISTER_KEY_PREFIX = "user:code:register:";
    private static final String MODIFY_KEY_PREFIX = "user:code:modify:";
    private static final String CHANGE_KEY_PREFIX = "user:code:change:";
    private final List<Integer> SEND_TYPE = new ArrayList<>( Arrays.asList( 1, 2 ) );
    private final List<Long> ROLE_ID_LIST = new ArrayList<>( Arrays.asList( 1L, 2L, 3L ) );
    private final List<Integer> CODE_TYPE = new ArrayList<>( Arrays.asList( 1, 2, 3, 4 ) );
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsConfig smsConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ScMapper scMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private JwtConfig config;
    @Autowired
    private HomeworkMapper homeworkMapper;
    @Autowired
    private HomeworkStudentMapper homeworkStudentMapper;

    @Override
    public QueryResponse findOneUser(long id) {
        User user = this.userMapper.findById( id );
        List<User> users = Collections.singletonList( user );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( users, users.size() ) );
    }

    @Override
    public QueryResponse findByPage(long currentPage, long pageSize, QueryPageRequestUser queryPageRequestUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNoneBlank( queryPageRequestUser.getRoleName() )) {
            Role role = this.roleMapper.selectOne( new QueryWrapper<Role>().eq( "r_name", queryPageRequestUser.getRoleName() ) );
            if (role != null) {
                queryPageRequestUser.setRoleId( role.getId() );
                queryWrapper.eq( "role_id", role.getId() );
            }
        }
        if (StringUtils.equals( "男", queryPageRequestUser.getGender() ) ||
                StringUtils.equals( "女", queryPageRequestUser.getGender() )) {
            queryWrapper.eq( "gender", queryPageRequestUser.getGender() );
        }
        if (StringUtils.isNoneBlank( queryPageRequestUser.getUsername() )) {
            queryPageRequestUser.setUsername( "%" + queryPageRequestUser.getUsername() + "%" );
            queryWrapper.like( "username", queryPageRequestUser.getUsername() );
        }
        if (StringUtils.isNoneBlank( queryPageRequestUser.getRealName() )) {
            queryPageRequestUser.setRealName( "%" + queryPageRequestUser.getRealName() + "%" );
            queryWrapper.like( "real_name", queryPageRequestUser.getRealName() );
        }
        if (StringUtils.isNoneBlank( queryPageRequestUser.getEmail() )) {
            queryPageRequestUser.setEmail( "%" + queryPageRequestUser.getEmail() + "%" );
            queryWrapper.like( "email", queryPageRequestUser.getEmail() );
        }
        if (StringUtils.isNoneBlank( queryPageRequestUser.getPhone() )) {
            queryPageRequestUser.setPhone( "%" + queryPageRequestUser.getPhone() + "%" );
            queryWrapper.like( "phone", queryPageRequestUser.getPhone() );
        }
        List<User> users = this.userMapper.findByPage( new Page<>( currentPage, pageSize ), queryPageRequestUser );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( users, this.userMapper.selectCount( queryWrapper ) ) );
    }

    @Override
    public QueryResponse findRolesByPage(long currentPage, long pageSize) {
        Page<Role> page = this.roleMapper.selectPage( new Page<>( currentPage, pageSize ), null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( page.getRecords(), (int) page.getTotal() ) );
    }

    @Override
    public QueryResponse findAllSchools() {
        List<School> schools = this.schoolMapper.selectList( null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( schools, schools.size() ) );
    }

    @Override
    public QueryResponse findSchoolsByPage(long currentPage, long pageSize) {
        Page<School> page = this.schoolMapper.selectPage( new Page<>( currentPage, pageSize ), null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( page.getRecords(), (int) page.getTotal() ) );
    }

    @Override
    public CommonResponse sendCode(LoginUser loginUser, int sendType, int codeType) {
        // 登录实体类或者 sendType 、codeType 为非法参数
        if (loginUser == null || !SEND_TYPE.contains( sendType ) || !CODE_TYPE.contains( codeType )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        User user;

        // 生成六位数字验证码
        String code = NumberUtils.generateCode( 6 );

        switch (sendType) {
            case 1: // 发送到手机
                // phone 为非法参数
                if (!ParamCheckUtils.checkPhone( loginUser.getPhone() )) {
                    return new CommonResponse( CommonCode.INVALID_PARAM );
                }
                user = this.userMapper.selectOne( new QueryWrapper<User>().eq( "phone", loginUser.getPhone() ) );

                // 如果是因登录和修改密码要求发送验证码到手机，先确认该账号已经注册过
                if ((codeType == 1 || codeType == 3) && user == null) {
                    return new CommonResponse( UserCode.PHONE_NOT_REGISTERED );
                }

                // 如果是因注册和换绑手机要求发送验证码到手机，先确认该账号尚未注册过
                if ((codeType == 2 || codeType == 4) && user != null) {
                    return new CommonResponse( UserCode.PHONE_HAS_BEEN_REGISTERED );
                }
                // 调用工具类发送验证码到手机
                try {
                    smsUtils.sendSms( loginUser.getPhone(), code, smsConfig.getSignName(), smsConfig.getVerifyCodeTemplate() );
                } catch (ClientException e) {
                    LOGGER.error( "发送验证码到用户手机异常！异常原因：{}", e );
                    ExceptionThrowUtils.cast( CommonCode.SERVER_ERROR );
                }
                break;
            case 2: // 发送到邮箱
                // email 为非法参数
                if (!ParamCheckUtils.checkEmail( loginUser.getEmail() )) {
                    return new CommonResponse( CommonCode.INVALID_PARAM );
                }
                user = this.userMapper.selectOne( new QueryWrapper<User>().eq( "email", loginUser.getEmail() ) );

                // 如果是因登录和修改密码要求发送验证码到邮箱，先确认该账号已经注册过
                if ((codeType == 1 || codeType == 3) && user == null) {
                    return new CommonResponse( UserCode.EMAIL_NOT_REGISTERED );
                }

                // 如果是因注册和换绑邮箱要求发送验证码到邮箱，先确认该账号尚未注册过
                if ((codeType == 2 || codeType == 4) && user != null) {
                    return new CommonResponse( UserCode.EMAIL_HAS_BEEN_REGISTERED );
                }

                try {
                    // 发送验证码
                    mailUtils.sendMail( loginUser.getEmail(), "【益学堂】验证码",
                            "【益学堂】您的验证码是" + code + "，用于验证身份、修改密码等，该验证码5分钟内有效，请勿向他人泄露。" );
                } catch (Exception e) {
                    LOGGER.error( "发送验证码到用户邮箱地址异常！异常原因：{}", e );
                    ExceptionThrowUtils.cast( CommonCode.SERVER_ERROR );
                }
        }

        // 将验证码存入 redis ，并设置过期时间为 5 分钟
        String key = sendType == 1 ? loginUser.getPhone() : loginUser.getEmail();
        switch (codeType) {
            case 1: // 登录验证码
                this.redisTemplate.opsForValue().set( LOGIN_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES );
                break;
            case 2: // 注册验证码
                this.redisTemplate.opsForValue().set( REGISTER_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES );
                break;
            case 3: // 修改密码验证码
                this.redisTemplate.opsForValue().set( MODIFY_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES );
                break;
            case 4: // 换绑邮箱/手机验证码
                this.redisTemplate.opsForValue().set( CHANGE_KEY_PREFIX + key, code, 5, TimeUnit.MINUTES );
                break;
        }
        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @Transactional
    public CommonResponse register(RegisterUser registerUser) {
        if (registerUser == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 0. 邮箱校验
        if (this.userMapper.selectOne( new QueryWrapper<User>().eq( "email", registerUser.getEmail() ) ) != null) {
            return new CommonResponse( UserCode.EMAIL_HAS_BEEN_REGISTERED );
        }
        // 1. 验证码校验
        if (!StringUtils.equals( registerUser.getCode(), this.redisTemplate.opsForValue().get( REGISTER_KEY_PREFIX + registerUser.getEmail() ) )) {
            return new CommonResponse( UserCode.REGISTER_FAIL_CODE_WRONG );
        }
        // 2. 用户名唯一性校验
        User foundUser = this.userMapper.selectOne( new QueryWrapper<User>().eq( "username", registerUser.getUsername() ) );
        if (foundUser != null) {
            return new CommonResponse( UserCode.REGISTER_FAIL_USERNAME_CONFLICT );
        }
        // 3. 角色名称校验
        Role role = this.roleMapper.selectOne( new QueryWrapper<Role>().eq( "r_name", registerUser.getRoleName() ) );
        if (role == null) {
            return new CommonResponse( UserCode.REGISTER_FAIL_ROLE_NAME_NOT_FOUND );
        }
        // 4. 将用户存入数据库
        User user = User.builder().id( null )
                .username( registerUser.getUsername() )
                .email( registerUser.getEmail() )
                .password( registerUser.getPassword() )
                .realName( registerUser.getRealName() )
                .school( registerUser.getSchool() )
                .createTime( new Date() )
                .updateTime( new Date() )
                .build();
        this.userMapper.insert( user );
        // 5. 更新用户的角色id信息
        this.userMapper.updateRoleIdByUsername( user.getUsername(), role.getId() );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @Transactional
    public CommonResponse updateUser(long id, UpdateUser updateUser) {

        // 1.参数验证
        if (updateUser == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 学/工号唯一性检验
        if (StringUtils.isNoneBlank( updateUser.getTsNo() )) {
            User oldUser = this.userMapper.selectOne( new QueryWrapper<User>().eq( "id", id ) );
            User foundUser = this.userMapper.selectOne( new QueryWrapper<User>().eq( "ts_no", updateUser.getTsNo() ) );
            if ((foundUser != null) && (!StringUtils.equals( foundUser.getTsNo(), oldUser.getTsNo() ))) { //若存在该学工号，判断是否是未修改前当前用户的学工号
                return new CommonResponse( UserCode.UPDATE_FAIL_TSNO_CONFLICT );
            }
        }

        // 3. 根据id获取修改前的用户信息，根据传入参数修改用户信息
        User user = this.userMapper.findById( id );
        if (StringUtils.isNoneBlank( updateUser.getRealName() )) {
            user.setRealName( updateUser.getRealName() );
        }
        if (StringUtils.isNoneBlank( updateUser.getGender() )) {
            user.setGender( updateUser.getGender() );
        }
        if (updateUser.getAge() > 0 && updateUser.getAge() < 120) {
            user.setAge( updateUser.getAge() );
        }
        if (StringUtils.isNoneBlank( updateUser.getSchool() )) {
            user.setSchool( updateUser.getSchool() );
        }
        if (StringUtils.isNoneBlank( updateUser.getTsNo() )) {
            user.setTsNo( updateUser.getTsNo() );
        }

        // 4. 更新最后一次修改个人信息时间
        user.setUpdateTime( new Date() );

        // 5. 更新用户信息
        this.userMapper.updateById( user );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @Transactional
    public CommonResponse updatePassword(long id, PasswordUser passwordUser) {

        // 1.参数验证
        if (passwordUser == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 验证码校验
        if (!StringUtils.equals( passwordUser.getCode(), this.redisTemplate.opsForValue().get( MODIFY_KEY_PREFIX + passwordUser.getEmail() ) )) {
            return new CommonResponse( UserCode.UPDATE_PASSWORD_FAIL_CODE_WRONG );
        }

        // 3. 更新个人信息
        User user = this.userMapper.selectById( id );
        user.setPassword( passwordUser.getNewPassword() );
        user.setUpdateTime( new Date() );
        this.userMapper.updateById( user );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @Transactional
    public CommonResponse updateEmail(long id, EmailUser emailUser) {

        // 1. 参数验证
        if (emailUser == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 新邮箱校验
        if (this.userMapper.selectOne( new QueryWrapper<User>().eq( "email", emailUser.getEmail() ) ) != null) {
            return new CommonResponse( UserCode.UPDATE_EMAIL_FAIL_EMAIL_ALREADY_EXISTS );
        }

        // 3. 验证码检验
        if (!StringUtils.equals( emailUser.getCode(), this.redisTemplate.opsForValue().get( CHANGE_KEY_PREFIX + emailUser.getEmail() ) )) {
            return new CommonResponse( UserCode.UPDATE_FAIL_CODE_WRONG );
        }

        // 4. 更新个人信息
        User user = this.userMapper.findById( id );
        user.setEmail( emailUser.getEmail() );
        user.setUpdateTime( new Date() );

        this.userMapper.updateById( user );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @Transactional
    public CommonResponse updatePhone(long id, PhoneUser phoneUser) {
        // 1. 参数验证
        if (phoneUser == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 2. 新手机号码校验
        if (this.userMapper.selectOne( new QueryWrapper<User>().eq( "phone", phoneUser.getPhone() ) ) != null) {
            return new CommonResponse( UserCode.PHONE_HAS_BEEN_REGISTERED );
        }

        // 3. 验证码检验
        if (!StringUtils.equals( phoneUser.getCode(), this.redisTemplate.opsForValue().get( CHANGE_KEY_PREFIX + phoneUser.getPhone() ) )) {
            return new CommonResponse( UserCode.UPDATE_FAIL_CODE_WRONG );
        }

        // 4. 更新用户信息
        User user = this.userMapper.selectById( id );
        user.setPhone( phoneUser.getPhone() );
        user.setUpdateTime( new Date() );
        this.userMapper.updateById( user );

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @Transactional
    public CommonResponse updateAvatar(long id, String avatar, HttpServletRequest request, HttpServletResponse response) {

        // 参数不合法
        if (StringUtils.isBlank( avatar )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 修改用户头像
        User user = this.userMapper.selectById( id );
        user.setAvatar( avatar );
        user.setUpdateTime( new Date() );
        this.userMapper.updateById( user );

        // 重新生成 token 信息并写回
        String cookieName = id == 3 ? config.getAdminCookieName() : config.getUserCookieName();
        String token = CookieUtils.getCookieValue( request, cookieName );
        try {
            // 重新生成 token 信息
            UserInfo userInfo = JwtUtils.getInfoFromToken( token, config.getPublicKey() );
            String newToken = JwtUtils.generateToken( new UserInfo( userInfo.getId(), userInfo.getUsername(),
                            userInfo.getIsTeacher(), avatar, userInfo.getRememberMe() ),
                    config.getPrivateKey(), config.getExpire() );
            // 重新写入cookie
            CookieUtils.setCookie( request, response, cookieName, newToken,
                    // 如果用户勾选了记住我，则将 cookie 存活时间设置为 7 天
                    userInfo.getRememberMe() ? 14 * config.getCookieMaxAge() : config.getCookieMaxAge(),
                    null, true );
        } catch (Exception e) {
            LOGGER.error( "解析token信息异常！异常原因：{}", e );
            return new CommonResponse( CommonCode.SERVER_ERROR );
        }

        return new CommonResponse( CommonCode.SUCCESS );

    }

    @Override
    @Transactional
    public CommonResponse delById(long userId) {
        User user = this.userMapper.findById( userId );
        long roleId = user.getRole().getId();
        if (!ROLE_ID_LIST.contains( roleId )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        if (roleId == 1) { // 管理员不允许被调用接口注销
            return CommonResponse.FAIL();
        } else if (roleId == 2) { // 如果注销的是教师用户，需先把其创建的课程删除
            List<Long> courseIds = this.courseMapper
                    .selectList( new QueryWrapper<Course>().eq( "teacher_id", userId )
                            .select( "id" ) ).stream().map( Course::getId ).collect( Collectors.toList() );
            courseIds.forEach( courseId -> {
                // 将选课表里关于该课程的记录删除
                this.scMapper.delete( new QueryWrapper<StudentCourse>().eq( "course_id", courseId ) );

                // 将该课程删除
                this.courseMapper.deleteById( courseId );
            } );
        } else { // 如果注销的是学生用户，需先将其选课记录删除
            this.scMapper.delete( new QueryWrapper<StudentCourse>().eq( "student_id", userId ) );
        }
        this.userMapper.deleteById( userId );
        return CommonResponse.SUCCESS();
    }

    @Override
    public QueryResponse findPageByCourseId(long courseId, long currentPage, long pageSize, String search) {

        // courseId 不合法
        Course course = this.courseMapper.findById( courseId );
        if (course == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 查询该门课程下的学生
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<StudentCourse>().eq( "course_id", courseId );
        List<Long> ids = this.scMapper.selectList( queryWrapper
                .select( "student_id" ) )
                .stream()
                .map( StudentCourse::getStudentId )
                .collect( Collectors.toList() );

        List<User> filterUsers = new ArrayList<>();
        // 如果 search 字段不为空，先在 User 总表里查询出符合条件的 User 列表，再将其中不是该门课程成员的 User 过滤掉
        // 得到的 filterUsers 即为符合搜索条件且为该门课程的成员列表
        if (StringUtils.isNoneBlank( search )) {
            QueryWrapper<User> userQueryWrapper =
                    new QueryWrapper<User>()
                            .like( "real_name", search )
                            .or()
                            .like( "ts_no", search );
            filterUsers = this.userMapper
                    .selectList( userQueryWrapper )
                    .stream()
                    .filter( user -> ids.contains( user.getId() ) )
                    .collect( Collectors.toList() );
            search = "%" + search + "%";
        }

        List<User> users = this.userMapper.findPageByIds( new Page<>( currentPage, pageSize ), ids, search );

        List<UserResp> userRespList = new ArrayList<>( users.size() );
        users.forEach( user -> {
            UserResp userResp = new UserResp();

            BeanUtils.copyProperties( user, userResp );

            userResp.setClazz( course.getClazz() );
            userResp.setJoinTime( this.scMapper.selectOne( new QueryWrapper<StudentCourse>()
                    .eq( "course_id", course.getId() )
                    .eq( "student_id", user.getId() )
                    .select( "join_time" ) ).getJoinTime() );

            userRespList.add( userResp );

        } );

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( userRespList,
                        StringUtils.isBlank( search )
                                ? ids.size() // 如果搜索字段为空，则 total 为总的课程成员
                                : filterUsers.size() ) ); // 否则为过滤后的成员

    }

    @Override
    public QueryResponse findPageByHomeworkId(long homeworkId, long currentPage, long pageSize, String search) {
        // homeworkId 不合法
        Homework homework = this.homeworkMapper.selectById( homeworkId );
        if (homework == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        QueryWrapper<HomeworkStudent> queryWrapper = new QueryWrapper<HomeworkStudent>().eq( "homework_id", homeworkId );
        List<Long> ids = this.homeworkStudentMapper.selectList( queryWrapper
                .select( "student_id" ) )
                .stream()
                .map( HomeworkStudent::getStudentId )
                .collect( Collectors.toList() );

        // 如果 search 字段不为空，先在 User 总表里查询出符合条件的 User 列表，再将其中不是该次作业成员的 User 过滤掉
        // 得到的 filterUsers 即为符合搜索条件且为该次作业的成员列表
        List<User> filterUsers = new ArrayList<>();
        if (StringUtils.isNoneBlank( search )) {
            QueryWrapper<User> userQueryWrapper =
                    new QueryWrapper<User>()
                            .like( "real_name", search )
                            .or()
                            .like( "ts_no", search );
            filterUsers = this.userMapper
                    .selectList( userQueryWrapper )
                    .stream()
                    .filter( user -> ids.contains( user.getId() ) )
                    .collect( Collectors.toList() );
            search = "%" + search + "%";
        }

        // 查询该次作业下的学生
        List<User> studentList = this.userMapper.findPageByHomeworkId( new Page<>( currentPage, pageSize ), homeworkId, search );

        if (!CollectionUtils.isEmpty( studentList )) {
            studentList.forEach(
                    student -> student.setHomeworkStudent( this.homeworkStudentMapper
                            .selectOne(
                                    new QueryWrapper<HomeworkStudent>()
                                            .eq( "homework_id", homeworkId )
                                            .eq( "student_id", student.getId() ) ) ) );
        }

        return new QueryResponse( CommonCode.SUCCESS,
                new QueryResult<>( studentList,
                        StringUtils.isBlank( search )
                                ? ids.size() // 如果搜索字段为空，则 total 为总的作业成员
                                : filterUsers.size() ) ); // 否则为过滤后的成员
    }
}
