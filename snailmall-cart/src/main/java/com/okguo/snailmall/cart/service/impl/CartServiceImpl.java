package com.okguo.snailmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.okguo.common.utils.R;
import com.okguo.snailmall.cart.config.MyThreadPoolConfig;
import com.okguo.snailmall.cart.feign.ProductFeignService;
import com.okguo.snailmall.cart.intercept.CartIntercept;
import com.okguo.snailmall.cart.service.CartService;
import com.okguo.snailmall.cart.vo.CartItem;
import com.okguo.snailmall.cart.vo.SkuInfoVo;
import com.okguo.snailmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/11 14:32
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ThreadPoolExecutor threadPool;
    @Autowired
    private ProductFeignService productFeignService;

    private static final String CART_PREFIX = "snailmall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        CartItem cartItem = new CartItem();

        CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
            R r = productFeignService.info(skuId);
            SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
            cartItem.setSkuId(skuId);
            cartItem.setCheck(true);
            cartItem.setCount(num);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setTitle(skuInfo.getSkuTitle());
            cartItem.setPrice(skuInfo.getPrice());
        }, threadPool);

        CompletableFuture<Void> getSaleAttrTask = CompletableFuture.runAsync(() -> {
            R skuSaleAttrs = productFeignService.getSkuSaleAttrStringList(skuId);
            List<String> skuAttr = skuSaleAttrs.getData("skuSaleAttrs", new TypeReference<List<String>>() {
            });
            cartItem.setSkuAttr(skuAttr);
        });

        CompletableFuture.allOf(getSkuInfoTask, getSaleAttrTask).get();
        hashOps.put(skuId.toString(), JSON.toJSONString(cartItem));

        return cartItem;
    }

    private BoundHashOperations<String, Object, Object> getHashOps() {
        ThreadLocal<UserInfoTo> threadLocal = CartIntercept.threadLocal;
        UserInfoTo userInfoTo = threadLocal.get();
        String cartKey;
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }
}
