package com.okguo.snailmall.cart.controller;

import com.alibaba.fastjson.JSON;
import com.okguo.common.constant.AuthServerConstant;
import com.okguo.snailmall.cart.intercept.CartIntercept;
import com.okguo.snailmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/11 15:47
 */
@Slf4j
@Controller
public class CartController {


    @GetMapping("cart.html")
    public String cartPage(HttpSession session) {

        UserInfoTo userInfoTo = CartIntercept.threadLocal.get();

        log.info(JSON.toJSONString(userInfoTo));


        return "cartList";
    }

    @GetMapping("addToCart")
    public String addToCart(){
        return "success";
    }


}
