package com.okguo.snailmall.auth.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/05 16:47
 */
@FeignClient("snailmall-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendCode")
    public R sendSmsCode(String mobile, String code);

}
