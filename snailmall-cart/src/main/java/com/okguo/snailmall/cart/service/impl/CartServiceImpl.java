package com.okguo.snailmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.okguo.common.utils.R;
import com.okguo.snailmall.cart.feign.ProductFeignService;
import com.okguo.snailmall.cart.intercept.CartIntercept;
import com.okguo.snailmall.cart.service.CartService;
import com.okguo.snailmall.cart.vo.Cart;
import com.okguo.snailmall.cart.vo.CartItem;
import com.okguo.snailmall.cart.vo.SkuInfoVo;
import com.okguo.snailmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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

        String res = (String) hashOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
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
        } else {
            CartItem cartItem = JSONObject.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            hashOps.put(skuId.toString(), JSON.toJSONString(cartItem));

            return cartItem;
        }
    }

    @Override
    public CartItem queryBySkuId(Long skuId) {
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        String cartItemStr = (String) hashOps.get(skuId.toString());
        return JSONObject.parseObject(cartItemStr, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartIntercept.threadLocal.get();
        String cartKey;
        List<CartItem> cartItems;
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> tempCartItems = changeCartItemsFromRedis(CART_PREFIX + userInfoTo.getUserKey());
            if (tempCartItems.size() > 0) {
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
            }
            this.clearCart(CART_PREFIX + userInfoTo.getUserKey());
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        cartItems = changeCartItemsFromRedis(cartKey);
        cart.setItems(cartItems);
        return cart;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        CartItem cartItem = queryBySkuId(skuId);
        cartItem.setCheck(check == 1);
        hashOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void checkNum(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        CartItem cartItem = queryBySkuId(skuId);
        cartItem.setCount(num);
        hashOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        hashOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentUserCartItems(Long memberId) {
        UserInfoTo userInfoTo = CartIntercept.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return new ArrayList<>();
        }
        String cartKey = CART_PREFIX + userInfoTo.getUserId();
        return getCartItems(cartKey).stream()
                .filter(CartItem::getCheck)
                .peek(e-> e.setPrice(productFeignService.getCurrentPrice(e.getSkuId())))
                .collect(Collectors.toList());
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            return values.stream().map((obj) -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItem.class);
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<CartItem> changeCartItemsFromRedis(String cartKey) {
        List<Object> cartItemStrings = redisTemplate.boundHashOps(cartKey).values();
        if (cartItemStrings != null && cartItemStrings.size() > 0) {
            return cartItemStrings.stream().map(obj -> JSONObject.parseObject((String) obj, CartItem.class)).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
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
