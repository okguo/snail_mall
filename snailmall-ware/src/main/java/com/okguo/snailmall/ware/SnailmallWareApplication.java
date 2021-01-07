package com.okguo.snailmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.okguo.snailmall.ware.dao")
@SpringBootApplication
public class SnailmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnailmallWareApplication.class, args);
    }

}
