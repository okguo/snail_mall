package com.okguo.snailmall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/05 14:43
 */
@Configuration
public class SnailMallWebConfig implements WebMvcConfigurer {

    /*
     * 功能类似于
     * //    @GetMapping("/login.html")
//    public String login(){
//        return "login";
//    }
     */

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("register");
    }
}
