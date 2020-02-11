package com.xuecheng.govern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author newHeart
 * @Create 2020/2/11 13:46
 */

@SpringBootApplication
@EnableEurekaServer
public class GovernCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernCenterApplication.class,args);
    }
}
