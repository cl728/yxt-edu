package com.yixuetang.intercept;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(FilterProperties.class)
public class AuthConfig implements WebMvcConfigurer {

    @Autowired
    private FilterProperties filterProperties;

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
//                .excludePathPatterns( this.filterProperties.getAllowPaths() ); // 排除拦截白名单中的api
    }

}
