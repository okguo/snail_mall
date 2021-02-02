package com.okguo.snailmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1.整合mybatis-plus
 *   1）、导入依赖
 *			<dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.3.1</version>
 *         </dependency>
 *   2）、配置
 *   	1、配置数据源
 *   	  1）、导入数据源
 *   	  2）、编辑配置文件
 *		2、配置mybatis-plus
 *		  1）、使用MapperScan
 *		  2）、高祖mybatis-plus xml文件位置
 */

@EnableCaching
@EnableFeignClients(basePackages = "com.okguo.snailmall.product.feign")
@SpringBootApplication
@MapperScan("com.okguo.snailmall.product.dao")
@EnableDiscoveryClient
public class SnailmallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnailmallProductApplication.class, args);
	}



}
