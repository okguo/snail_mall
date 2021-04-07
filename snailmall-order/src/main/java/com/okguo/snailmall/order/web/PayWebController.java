package com.okguo.snailmall.order.web;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.okguo.snailmall.order.config.AlipayTemplate;
import com.okguo.snailmall.order.entity.OrderEntity;
import com.okguo.snailmall.order.service.OrderService;
import com.okguo.snailmall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class PayWebController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.queryOrderByOrderSn(orderSn);

        String pay = alipayTemplate.pay(payVo);

        log.info("payOrder->" + JSON.toJSONString(pay));
        return pay;
    }

}
