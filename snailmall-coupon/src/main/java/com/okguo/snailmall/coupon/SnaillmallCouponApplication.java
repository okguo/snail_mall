package com.okguo.snailmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.okguo.snailmall.coupon.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class SnaillmallCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnaillmallCouponApplication.class, args);
	}

}
