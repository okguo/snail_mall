package com.okguo.snailmall.thridparty.controller;

import com.okguo.common.utils.R;
import com.okguo.snailmall.thridparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/05 16:37
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    @GetMapping("/sendCode")
    public R sendSmsCode(@RequestParam("mobile") String mobile, @RequestParam("code") String code) {
        boolean b = smsComponent.sendSmsCode(mobile, code);
        if (b) {
            return R.ok();
        }
        return R.error();
    }


}
