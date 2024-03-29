package com.okguo.snailmall.order.web;

import com.alibaba.fastjson.JSON;
import com.okguo.common.exception.RRException;
import com.okguo.snailmall.order.entity.OrderEntity;
import com.okguo.snailmall.order.service.OrderService;
import com.okguo.snailmall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
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
    RabbitTemplate rabbitTemplate;
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
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes redirectAttributes) {
        try {

            log.info("提交订单参数：" + JSON.toJSONString(submitVo));
            SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
            model.addAttribute("responseVo", responseVo);

            if (responseVo.getCode() == 0) {
                model.addAttribute("submitOrderResp", responseVo);
                return "pay";
            }

            String msg = "下单失败：";
            switch (responseVo.getCode()) {
                case 1:
                    msg += "订单信息过期，请刷新后再次提交";
                    break;
                case 2:
                    msg += "订单价格发生变化，请确认后再次提交";
                    break;
                case 3:
                    msg += "库存锁定失败，商品库存不足";
                    break;
            }
            redirectAttributes.addFlashAttribute("msg", msg);
            return "redirect:http://order.snailmall.com/toTrade";
        } catch (Exception e) {
            if (e instanceof RRException) {
                redirectAttributes.addFlashAttribute("msg", ((RRException) e).getMsg());
            }
            return "redirect:http://order.snailmall.com/toTrade";
        }
    }

    @ResponseBody
    @RequestMapping("/createOrder")
    public String createOrder() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", JSON.toJSONString(orderEntity));
        return "ok";
    }
}
