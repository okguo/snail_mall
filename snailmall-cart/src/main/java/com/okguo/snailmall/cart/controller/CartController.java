package com.okguo.snailmall.cart.controller;

import com.alibaba.fastjson.JSON;
import com.okguo.common.constant.AuthServerConstant;
import com.okguo.snailmall.cart.intercept.CartIntercept;
import com.okguo.snailmall.cart.service.CartService;
import com.okguo.snailmall.cart.vo.Cart;
import com.okguo.snailmall.cart.vo.CartItem;
import com.okguo.snailmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/11 15:47
 */
@Slf4j
@Controller
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("cart.html")
    public String cartPage(HttpSession session,Model model) {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    @GetMapping("addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes model) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        model.addAttribute("skuId", skuId);
        return "redirect:http://cart.snailmall.com/addToCartSuccess.html";
    }

    @GetMapping("addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId,Model model) {
        CartItem cartItem = cartService.queryBySkuId(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }


}
