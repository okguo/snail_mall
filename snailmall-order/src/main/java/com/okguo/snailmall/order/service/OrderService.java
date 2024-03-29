package com.okguo.snailmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.to.mq.SeckillOrderTo;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.order.entity.OrderEntity;
import com.okguo.snailmall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:58:41
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);

    OrderEntity getOrderStatus(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo queryOrderByOrderSn(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo vo);

    void orderSeckillCreate(SeckillOrderTo seckillOrderTo);
}

