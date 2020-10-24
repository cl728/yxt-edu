package com.yixuetang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author Colin
 * @version 1.0.0
 * @description 益学堂服务启动程序
 * @date 2020/10/23 13:17
 */
@SpringBootApplication
public class YxtServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run( YxtServiceApplication.class, args );
    }

    /**
     * 配置跨域请求信息
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1.添加 CORS 配置信息
        CorsConfiguration config = new CorsConfiguration();
        // 1.1 允许的域,不要写 * ，否则 cookie 就无法使用了
        config.addAllowedOrigin( "http://www.yixuetang.com" );
        // 1.2 是否发送Cookie信息
        config.setAllowCredentials( true );
        // 1.3 允许的请求方式
        config.addAllowedMethod( "OPTIONS" );
        config.addAllowedMethod( "HEAD" );
        config.addAllowedMethod( "GET" );
        config.addAllowedMethod( "PUT" );
        config.addAllowedMethod( "POST" );
        config.addAllowedMethod( "DELETE" );
        config.addAllowedMethod( "PATCH" );
        // 1.4 允许的头信息
        config.addAllowedHeader( "*" );

        // 2.添加映射路径，这里拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration( "/**", config );

        // 3.返回新的CorsFilter.
        return new CorsFilter( configSource );
    }
}
