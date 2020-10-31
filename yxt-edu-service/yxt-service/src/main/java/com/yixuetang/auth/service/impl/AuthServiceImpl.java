package com.yixuetang.auth.service.impl;

import com.yixuetang.auth.service.AuthService;
import com.yixuetang.entity.auth.UserInfo;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.auth.JwtConfig;
import com.yixuetang.utils.auth.JwtUtils;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Colin
 * @version 1.0.0
 * @description 认证模块服务层接口实现类
 * @date 2020/10/23 21:07
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtConfig config;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LOGIN_KEY_PREFIX = "user:code:login:";

    private static final Logger LOGGER = LoggerFactory.getLogger( AuthServiceImpl.class );

    @Override
    public String authByPwd(String username, String password, boolean rememberMe, int userType) {
        User user = this.userMapper.findByUsernameAndPassword( username, password );
        return getToken( user, rememberMe, userType );
    }

    @Override
    public String authByEmail(String email, String code, boolean rememberMe, int userType) {
        // 验证码校验
        if (!StringUtils.equals( code, this.redisTemplate.opsForValue().get( LOGIN_KEY_PREFIX + email ) )) {
            return null;
        }

        User user = this.userMapper.findByEmail( email );

        return getToken( user, rememberMe, userType );
    }

    @Override
    public String authByPhone(String phone, String code, boolean rememberMe, int userType) {
        // 验证码校验
        if (!StringUtils.equals( code, this.redisTemplate.opsForValue().get( LOGIN_KEY_PREFIX + phone ) )) {
            return null;
        }

        User user = this.userMapper.findByPhone( phone );

        return getToken( user, rememberMe, userType );
    }

    private String getToken(User user, boolean rememberMe, int userType) {
        if (user == null) {
            return null;
        }
        // 校验是否合法登录
        if (!checkIfValid( userType, user.getRole().getId() )) {
            return null;
        }
        String token = null;
        // 如果有查询结果，则生成token
        try {
            token = JwtUtils.generateToken( new UserInfo( user.getId(), user.getUsername(),
                            user.getRole().getId() == 2, user.getAvatar(), rememberMe ),
                    config.getPrivateKey(), config.getExpire() );
        } catch (Exception e) {
            LOGGER.error( "生成 token 发生异常！异常原因：{}", e.getMessage() );
            ExceptionThrowUtils.cast( CommonCode.SERVER_ERROR );
        }
        return token;
    }

    private boolean checkIfValid(int userType, Long roleId) {
        switch (userType) {
            case 1: // 管理员登录
                if (roleId != 1) {
                    return false;
                }
                break;
            case 2: // 普通用户登录
                if (roleId != 2 && roleId != 3) {
                    return false;
                }
        }
        return true;
    }
}
