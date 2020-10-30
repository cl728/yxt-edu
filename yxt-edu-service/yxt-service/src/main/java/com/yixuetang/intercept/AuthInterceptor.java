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
        String token = null;
        String requestURI = request.getRequestURI();
        /*
          需要鉴权为管理员才能执行的接口：
          - 包含 admin 的接口
          - 与 swagger 测试页面相关的请求和接口
          - 管理员退出登录接口
          - 分页查询课程接口
          - 分页查询用户列表接口
         */
        if (StringUtils.contains( requestURI, "admin" ) ||
                StringUtils.contains( requestURI, "swagger" ) ||
                StringUtils.contains( requestURI, "/auth/logout/1" ) ||
                StringUtils.contains( requestURI, "/courses/page" ) ||
                StringUtils.contains( requestURI, "/users/page" )) {
            token = CookieUtils.getCookieValue( request, jwtConfig.getAdminCookieName() );

        /*
          需要鉴权为普通用户才能执行的接口
          - 普通用户退出登录接口
          - 普通用户修改个人信息接口
          - 普通用户修改密码接口
          - 普通用户换绑邮箱接口
          - 普通用户换绑手机号码接口
         */
        } else if (StringUtils.contains( requestURI, "/auth/logout/2" ) ||
                StringUtils.contains( requestURI, "/users/password" ) ||
                StringUtils.contains( requestURI, "/users/email" ) ||
                StringUtils.contains( requestURI, "/users/info" ) ||
                StringUtils.contains( requestURI, "/users/phone" ))
            token = CookieUtils.getCookieValue( request, jwtConfig.getUserCookieName() );
        // 进行鉴权，如果解析成功则放行
        try {
            JwtUtils.getInfoFromToken( token, jwtConfig.getPublicKey() );
            return true;
        } catch (Exception e) {
            response.sendError( HttpStatus.UNAUTHORIZED.value() );
        }
        return false;
    }
}
