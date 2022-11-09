package com.tensquare.encrypt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author Ykj
 * @date 2022/11/6/14:55
 * @apiNote
 */
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class EncryptApplcation {
    public static void main(String[] args) {
        SpringApplication.run(EncryptApplcation.class,args);
    }
}
