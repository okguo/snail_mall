package com.okguo.snailmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.okguo.snailmall.member.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class SnailmallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnailmallMemberApplication.class, args);
	}

}
