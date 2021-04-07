package com.okguo.snailmall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberOrderController {

    @GetMapping("orderList.html")
    public String memberOrder() {
        return "orderList";
    }


}
