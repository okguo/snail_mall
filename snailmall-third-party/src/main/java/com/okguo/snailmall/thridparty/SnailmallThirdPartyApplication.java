package com.okguo.snailmall.thridparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SnailmallThirdPartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnailmallThirdPartyApplication.class, args);
	}

}
