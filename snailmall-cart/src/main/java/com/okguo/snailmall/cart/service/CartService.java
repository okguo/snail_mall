package com.okguo.snailmall.cart.service;

import com.okguo.snailmall.cart.vo.Cart;
import com.okguo.snailmall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/11 14:32
 */
public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem queryBySkuId(Long skuId);

    Cart getCart();

}
