package com.tensquare.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Ykj
 * @date 2022/11/6/13:13
 * @apiNote
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplcation {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplcation.class,args);
    }
}
