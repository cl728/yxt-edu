package com.yixuetang.auth.controller;

import com.yixuetang.api.auth.AuthControllerApi;
import com.yixuetang.auth.service.AuthService;
import com.yixuetang.entity.auth.UserInfo;
import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.auth.AuthCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.utils.auth.JwtConfig;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.utils.auth.CookieUtils;
import com.yixuetang.utils.auth.JwtUtils;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * @author Colin
 * @version 1.0.0
 * @description 认证模块控制层类
 * @date 2020/10/23 20:56
 */
@RestController
@EnableConfigurationProperties(JwtConfig.class)
@RequestMapping("auth")
public class AuthController implements AuthControllerApi {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtConfig config;

    private static final Logger LOGGER = LoggerFactory.getLogger( AuthController.class );

    /**
     * 处理用户登录逻辑
     *
     * @param loginType 登录方式，1 账号登录 2 邮箱登录
     * @param loginUser 登录用户实体类
     * @param request   request
     * @param response  response
     * @return 响应实体类
     */
    @Override
    @PostMapping("login/{loginType}")
    public CommonResponse login(@PathVariable int loginType, @RequestBody LoginUser loginUser,
                                HttpServletRequest request, HttpServletResponse response) {
        // loginType 和 loginUser 非法
        if ((loginType != 1 && loginType != 2) || loginUser == null) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        String token = null;
        switch (loginType) {
            case 1: // 账号登录
                // 登录校验
                token = this.authService.authByPwd( loginUser.getUsername(), loginUser.getPassword(), loginUser.isRememberMe() );
                if (StringUtils.isBlank( token )) {
                    return new CommonResponse( AuthCode.LOGIN_FAIL_BY_PWD );
                }
                break;
            case 2: // 邮箱登录
                // 登录校验
                token = this.authService.authByEmail( loginUser.getEmail(), loginUser.getCode(), loginUser.isRememberMe() );
                if (StringUtils.isBlank( token )) {
                    return new CommonResponse( AuthCode.LOGIN_FAIL_BY_EMAIL );
                }
                break;
        }
        // 将 token 写入 cookie ,并指定 httpOnly 为 true ，防止通过 JS 获取和修改
        CookieUtils.setCookie( request, response, config.getCookieName(), token,
                // 如果用户勾选了下次自动登录，则将 cookie 存活时间设置为 7 天
                loginUser.isRememberMe() ? 14 * config.getCookieMaxAge() : config.getCookieMaxAge(),
                null, true );
        return new CommonResponse( AuthCode.LOGIN_SUCCESS );
    }

    /**
     * 根据用户携带的 token 信息验证用户身份
     *
     * @param token    用户携带的 token 信息
     * @param request  request
     * @param response response
     * @return 响应实体类
     */
    @Override
    @GetMapping("verify")
    public CommonResponse verify(@CookieValue(name = "YXT_USER_AUTH") String token,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            // 解析 token
            UserInfo userInfo = JwtUtils.getInfoFromToken( token, config.getPublicKey() );
            // 解析成功要重新刷新 token
            token = JwtUtils.generateToken( userInfo, config.getPrivateKey(), config.getExpire() );
            // 更新 cookie 中的 token
            CookieUtils.setCookie( request, response, config.getCookieName(), token,
                    userInfo.getRememberMe() ? 14 * config.getCookieMaxAge() : config.getCookieMaxAge(),
                    null, true );

            return new QueryResponse( AuthCode.VERIFY_SUCCESS,
                    new QueryResult<>( Collections.singletonList( userInfo ), 1 ) );
        } catch (Exception e) {
            LOGGER.error( "获取token中的用户信息异常！异常原因：{}", e );
        }
        return new CommonResponse( CommonCode.SERVER_ERROR );
    }

    /**
     * 用户退出登录
     *
     * @param request  request
     * @param response response
     * @return 响应结果实体类
     */
    @Override
    @DeleteMapping("logout")
    public CommonResponse logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.setCookie( request, response, config.getCookieName(),
                "VALID VALUE", 1, null, true );
        return new CommonResponse( CommonCode.SUCCESS );
    }
}
