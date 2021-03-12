package com.okguo.snailmall.cart.config;

import com.okguo.snailmall.cart.intercept.CartIntercept;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/11 19:23
 */
@Configuration
public class SnailmallWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartIntercept()).addPathPatterns("/**");
    }
}
