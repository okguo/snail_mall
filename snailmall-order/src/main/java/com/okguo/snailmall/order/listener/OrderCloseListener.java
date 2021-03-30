package com.okguo.snailmall.order.listener;

import com.alibaba.fastjson.JSONObject;
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

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/30 19:52
 */
@Slf4j
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void testListen(String orderString, Channel channel, Message message) throws IOException {
        OrderEntity orderEntity = JSONObject.parseObject(orderString, OrderEntity.class);
        log.info("SnailMallOrderRabbitMQConfig->收到过期订单的信息，准备关闭订单，订单号:" + orderEntity.getOrderSn());
        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }


}
