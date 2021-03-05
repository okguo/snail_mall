package com.okguo.snailmall.auth.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/05 16:47
 */
@FeignClient("snailmall-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendCode")
    public R sendSmsCode(@RequestParam("mobile") String mobile, @RequestParam("code") String code);

}
