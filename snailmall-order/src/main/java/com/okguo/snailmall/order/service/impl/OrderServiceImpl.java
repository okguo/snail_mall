package com.okguo.snailmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.okguo.common.utils.R;
import com.okguo.common.vo.MemberVO;
import com.okguo.snailmall.order.feign.CartFeignService;
import com.okguo.snailmall.order.feign.MemberFeignService;
import com.okguo.snailmall.order.feign.WareFeignService;
import com.okguo.snailmall.order.interceptor.LoginUserInterceptor;
import com.okguo.snailmall.order.vo.MemberAddressVo;
import com.okguo.snailmall.order.vo.OrderConfirmVo;
import com.okguo.snailmall.order.vo.OrderItemVo;
import com.okguo.snailmall.order.vo.SkuStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.order.dao.OrderDao;
import com.okguo.snailmall.order.entity.OrderEntity;
import com.okguo.snailmall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private LoginUserInterceptor loginUserInterceptor;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        MemberVO memberVO = loginUserInterceptor.threadLocal.get();
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //异步编排保证每个线程都共享cookie
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r = memberFeignService.queryMemberAddressByMemberId(memberVO.getId());
            if (r.getCode() == 0) {
                List<MemberAddressVo> addressList = r.getData("list", new TypeReference<List<MemberAddressVo>>() {
                });
                orderConfirmVo.setAddress(addressList);
            }
        }, executor);

        CompletableFuture<Void> orderItemFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r1 = cartFeignService.currentUserCartItems(memberVO.getId());
            if (r1.getCode() == 0) {
                List<OrderItemVo> cartItems = r1.getData("cartItems", new TypeReference<List<OrderItemVo>>() {
                });
                orderConfirmVo.setItems(cartItems);
            }
        }, executor).thenRunAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<Long> collect = orderConfirmVo.getItems().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.hasStock(collect);
            List<SkuStockVo> data = r.getData("data", new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null && data.size() > 0) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                orderConfirmVo.setStocks(map);
            }
        }, executor);


        orderConfirmVo.setIntegration(memberVO.getIntegration());
        CompletableFuture.allOf(addressFuture, orderItemFuture).get();

        return orderConfirmVo;
    }

}