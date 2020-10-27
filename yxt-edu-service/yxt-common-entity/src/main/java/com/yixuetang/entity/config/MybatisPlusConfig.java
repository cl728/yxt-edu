package com.yixuetang.entity.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Colin
 * @version 1.0.0
 * @description MyBaits-Plus 分页插件配置类
 * @date 2020/10/27 16:01
 */
@Configuration
@MapperScan("com.yixuetang.*.mapper*")
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true 调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setLimit( 100 );
        // 开启 count 的 join 优化, 只针对部分 left join
        paginationInterceptor.setCountSqlParser( new JsqlParserCountOptimize( true ) );
        return paginationInterceptor;
    }
}