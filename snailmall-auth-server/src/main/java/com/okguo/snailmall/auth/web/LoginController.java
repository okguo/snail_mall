package com.okguo.snailmall.auth.web;

import com.okguo.common.utils.R;
import com.okguo.snailmall.auth.feign.ThirdPartFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/04 21:21
 */
@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("mobile") String mobile) {

        String code = UUID.randomUUID().toString().substring(0, 5);

        thirdPartFeignService.sendSmsCode(mobile, code);

        return R.ok();
    }


}
