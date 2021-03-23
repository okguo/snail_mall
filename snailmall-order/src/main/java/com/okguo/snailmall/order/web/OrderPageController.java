package com.okguo.snailmall.order.web;

import com.alibaba.fastjson.JSON;
import com.okguo.snailmall.order.service.OrderService;
import com.okguo.snailmall.order.vo.OrderConfirmVo;
import com.okguo.snailmall.order.vo.OrderSubmitVo;
import com.okguo.snailmall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/17 15:42
 */
@Slf4j
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

    @PostMapping("submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model) {
        log.info("提交订单参数：" + JSON.toJSONString(submitVo));
        SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
        model.addAttribute("responseVo", responseVo);

        if (responseVo.getCode() == 0) {
            return "pay";
        }
        return "redirect:http://order.snailmall.com/toTrade";
    }

}
