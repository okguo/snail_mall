package com.okguo.snailmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.okguo.snailmall.coupon.dao")
@SpringBootApplication
public class SnaillmallCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnaillmallCouponApplication.class, args);
	}

}
