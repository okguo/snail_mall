package com.okguo.snailmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.okguo.snailmall.member.dao")
@EnableFeignClients(basePackages = "com.okguo.snailmall.member.feign")
public class SnailmallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnailmallMemberApplication.class, args);
	}

}
