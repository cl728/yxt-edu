package com.yixuetang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixuetang.auth.service.AuthService;
import com.yixuetang.entity.auth.UserInfo;
import com.yixuetang.utils.auth.JwtConfig;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.UserMapper;
import com.yixuetang.utils.auth.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger LOGGER = LoggerFactory.getLogger( AuthServiceImpl.class );

    @Override
    public String authByPwd(String username, String password) {
        User user = this.userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq( "username", username )
                        .eq( "password", password ) );
        if (user == null) {
            return null;
        }
        String token = null;
        // 如果有查询结果，则生成token
        try {
            token = JwtUtils.generateToken( new UserInfo( user.getId(), user.getUsername(),
                            user.getRealName(), user.getAvatar() ),
                    config.getPrivateKey(), config.getExpire() );
        } catch (Exception e) {
            LOGGER.error( "生成 token 发生异常！异常原因：{}", e.getMessage() );
        }
        return token;
    }

    @Override
    public String authByEmail(String email, String code) {
        return null;
    }
}
