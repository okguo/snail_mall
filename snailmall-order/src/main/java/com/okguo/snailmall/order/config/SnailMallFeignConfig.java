package com.okguo.snailmall.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/22 16:06
 */
@Configuration
public class SnailMallFeignConfig {

    /**
     * 处理feign远程调用请求头失效问题
     *
     * @Author: Guoyongfu
     * @Date: 2021/3/22 16:22
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String cookie = request.getHeader("Cookie");
            template.header("Cookie", cookie);
        };
    }

}
