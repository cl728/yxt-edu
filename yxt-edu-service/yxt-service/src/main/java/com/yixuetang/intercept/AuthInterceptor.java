package com.yixuetang.intercept;

import com.yixuetang.utils.auth.CookieUtils;
import com.yixuetang.utils.auth.JwtConfig;
import com.yixuetang.utils.auth.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description 授权拦截器，检验用户是否已经被授权
 * @date 2020/10/27 22:03
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token;
        String requestURI = request.getRequestURI();
        if (StringUtils.contains( requestURI, "admin" ) ||
                StringUtils.contains( requestURI, "swagger" ) ||
                StringUtils.contains( requestURI, "/auth/logout/1" )) {
            // 如果请求路径包含 admin 和 swagger 测试以及管理员退出登录, 对管理员身份进行鉴权
            token = CookieUtils.getCookieValue( request, jwtConfig.getAdminCookieName() );
        } else {
            // 否则对普通用户身份进行鉴权
            token = CookieUtils.getCookieValue( request, jwtConfig.getUserCookieName() );
        }

        try {
            JwtUtils.getInfoFromToken( token, jwtConfig.getPublicKey() );
            return true;
        } catch (Exception e) {
            response.sendError( HttpStatus.UNAUTHORIZED.value() );
        }
        return false;
    }
}
