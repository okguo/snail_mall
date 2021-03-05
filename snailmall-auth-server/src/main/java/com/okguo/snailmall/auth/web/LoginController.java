package com.okguo.snailmall.auth.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/04 21:21
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "index";
    }
}
