package com.okguo.snailmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.okguo.snailmall.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class SnailmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnailmallWareApplication.class, args);
    }

}
