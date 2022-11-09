package com.tensquare.article;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import util.IdWorker;

/**
 * @author Ykj
 * @date 2022/11/1/22:59
 * @apiNote
 */

@MapperScan("com.tensquare.article.dao")
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ArticleApplication  {
   public static void main(String[] args) {
      SpringApplication.run(ArticleApplication.class,args);
   }
   
   @Bean
   public IdWorker createIdWorker() {
      return new IdWorker(1, 1);
   }
}
