package com.tensquare.notice.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ykj
 * @date 2022/11/6/19:37
 * @apiNote
 */

@Configuration
@MapperScan("com.tensquare.notice.dao")
public class MyBatisPlusConfig {
    
    @Bean
    public PaginationInterceptor createPaginationInterceptor() {
        return new PaginationInterceptor();
    }
}
