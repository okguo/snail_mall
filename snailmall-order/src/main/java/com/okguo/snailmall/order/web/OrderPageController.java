package com.okguo.snailmall.order.web;

import com.okguo.snailmall.order.service.OrderService;
import com.okguo.snailmall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/17 15:42
 */
@Controller
public class OrderPageController {

    @Autowired
    private OrderService orderService;

    @GetMapping("confirm")
    private String confirmPage() {
        return "confirm";
    }

    @GetMapping("detail")
    private String detailPage() {
        return "detail";
    }

    @GetMapping("list")
    private String listPage() {
        return "list";
    }

    @GetMapping("pay")
    private String payPage() {
        return "pay";
    }

    @GetMapping("toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

}
