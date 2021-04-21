package com.okguo.snailmall.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.okguo.common.to.mq.SeckillOrderTo;
import com.okguo.snailmall.order.entity.OrderEntity;
import com.okguo.snailmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Service
public class OrderSeckillListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void orderSeckillListen(String seckillOrderToString, Channel channel, Message message) throws IOException {
        SeckillOrderTo seckillOrderTo = JSONObject.parseObject(seckillOrderToString, SeckillOrderTo.class);
        log.info("OrderSeckillListener->收到秒杀订单消息，准备创建订单，订单号:" + seckillOrderTo.getOrderSn());
        try {
            orderService.orderSeckillCreate(seckillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
