package com.okguo.snailmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.okguo.snailmall.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.okguo.snailmall.ware.feign")
public class SnailmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnailmallWareApplication.class, args);
    }

}
