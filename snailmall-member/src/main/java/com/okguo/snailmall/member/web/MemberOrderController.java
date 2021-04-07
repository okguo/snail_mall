package com.okguo.snailmall.member.web;

import com.alibaba.fastjson.JSON;
import com.okguo.common.utils.R;
import com.okguo.snailmall.member.feign.OrderFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class MemberOrderController {

    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("/orderList.html")
    public String memberOrder(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(pageNum));
        R r = orderFeignService.listWithItem(params);
        log.info("MemberOrderController->memberOrder:"+ JSON.toJSONString(r));
        model.addAttribute("orders", r);
        return "order";
    }


}
