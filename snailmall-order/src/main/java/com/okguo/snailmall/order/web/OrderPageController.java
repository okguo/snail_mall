package com.okguo.snailmall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/17 15:42
 */
@Controller
public class OrderPageController {

    @RequestMapping("confirm")
    private String confirmPage() {
        return "confirm";
    }

    @RequestMapping("detail")
    private String detailPage() {
        return "detail";
    }

    @RequestMapping("list")
    private String listPage() {
        return "list";
    }

    @RequestMapping("pay")
    private String payPage() {
        return "pay";
    }

}
