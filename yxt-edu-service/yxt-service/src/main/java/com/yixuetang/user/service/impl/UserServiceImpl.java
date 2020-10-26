package com.yixuetang.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.user.UserCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.entity.user.Role;
import com.yixuetang.entity.user.School;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.RoleMapper;
import com.yixuetang.user.mapper.SchoolMapper;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.user.service.UserService;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import com.yixuetang.utils.user.MailUtils;
import com.yixuetang.utils.user.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块服务层接口实现类
 * @date 2020/10/23 16:09
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private SchoolMapper schoolMapper;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final List<Integer> CODE_TYPE = new ArrayList<>( Arrays.asList( 1, 2, 3 ) );

    private static final Logger LOGGER = LoggerFactory.getLogger( UserServiceImpl.class );

    private static final String LOGIN_KEY_PREFIX = "user:code:login:";
    private static final String REGISTER_KEY_PREFIX = "user:code:register:";
    private static final String MODIFY_KEY_PREFIX = "user:code:modify:";

    @Override
    public QueryResponse findAll() {
        List<User> users = this.userMapper.findAll();
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( users, users.size() ) );
    }

    @Override
    public QueryResponse findAllRoles() {
        List<Role> roles = this.roleMapper.selectList( null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( roles, roles.size() ) );
    }

    @Override
    public QueryResponse findAllSchools() {
        List<School> schools = this.schoolMapper.selectList( null );
        return new QueryResponse( CommonCode.SUCCESS, new QueryResult<>( schools, schools.size() ) );
    }

    @Override
    public CommonResponse sendCode(String email, int codeType) {
        // 邮箱地址或者 type 为非法参数
        if (StringUtils.isBlank( email ) || !CODE_TYPE.contains( codeType )) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }

        // 如果是因登录和修改密码要求发送验证码，先确认该账号已经注册过
        if (codeType == 1 || codeType == 2) {
            User user = this.userMapper.selectOne( new QueryWrapper<User>().eq( "email", email ) );
            if (user == null) {
                return new CommonResponse( UserCode.USER_NOT_REGISTER );
            }
        }

        // 生成六位数字验证码
        String code = NumberUtils.generateCode( 6 );
        try {
            // 发送验证码
            mailUtils.sendMail( email, "【益学堂】验证码",
                    "【益学堂】您的验证码是" + code + "，用于验证身份、修改密码等，该验证码5分钟内有效，请勿向他人泄露。" );
        } catch (Exception e) {
            LOGGER.error( "发送验证码异常！异常原因：{}", e );
            ExceptionThrowUtils.cast( CommonCode.SERVER_ERROR );
        }

        // 将验证码存入 redis ，并设置过期时间为 5 分钟
        switch (codeType) {
            case 1: // 登录验证码
                this.redisTemplate.opsForValue().set( LOGIN_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES );
                break;
            case 2: // 注册验证码
                this.redisTemplate.opsForValue().set( REGISTER_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES );
                break;
            case 3: // 修改密码验证码
                this.redisTemplate.opsForValue().set( MODIFY_KEY_PREFIX + email, code, 5, TimeUnit.MINUTES );
                break;
        }
        return new CommonResponse( CommonCode.SUCCESS );
    }
}
