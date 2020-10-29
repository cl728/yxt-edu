package com.yixuetang.intercept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/10/27 22:05
 */
@Configuration
public class AuthConfig implements WebMvcConfigurer {

    /**
     * 解决拦截器无法注入bean的问题
     */
    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor( authInterceptor() )
//                .addPathPatterns( "/**" )
//                .excludePathPatterns( "/auth/login/**" ) // 排除登录授权接口
//                .excludePathPatterns( "/auth/verify/**" ) // 排除鉴权接口
//                .excludePathPatterns( "/users/register" ) // 排除用户注册接口
//                .excludePathPatterns( "/users/schools" ) // 排除查询学校列表接口
//                .excludePathPatterns( "/users/code/*" ); // 排除发送验证码接口
    }

}
