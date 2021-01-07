package com.okguo.snailmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.okguo.snailmall.member.dao")
@SpringBootApplication
public class SnailmallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnailmallMemberApplication.class, args);
	}

}
