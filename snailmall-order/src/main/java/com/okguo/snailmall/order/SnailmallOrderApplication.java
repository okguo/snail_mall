package com.okguo.snailmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SnailmallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnailmallOrderApplication.class, args);
	}

}
