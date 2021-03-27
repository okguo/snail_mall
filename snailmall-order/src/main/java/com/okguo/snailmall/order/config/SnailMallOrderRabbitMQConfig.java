package com.okguo.snailmall.order.config;

import com.alibaba.fastjson.JSONObject;
import com.okguo.snailmall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/26 17:36
 */
@Slf4j
@Configuration
public class SnailMallOrderRabbitMQConfig {

    @RabbitListener(queues = "order.release.order.queue")
    public void testListen(String orderString, Channel channel, Message message) throws IOException {
        OrderEntity orderEntity = JSONObject.parseObject(orderString, OrderEntity.class);
        log.info("SnailMallOrderRabbitMQConfig->收到过期订单的信息，准备关闭订单，订单号:" + orderEntity.getOrderSn());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }


    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        //测试环境设置1分钟
        arguments.put("x-message-ttl", 60000);
        //String name, boolean durable, boolean exclusive, boolean autoDelete,@Nullable Map<String, Object> arguments
        return new Queue("order.delay.queue", true, false, false, arguments);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue");
    }

    @Bean
    public Exchange orderEventExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrderBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,@Nullable Map<String, Object> arguments
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.release.order", null);
    }

}
