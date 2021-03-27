package com.okguo.snailmall.ware.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/27 16:30
 */
@Configuration
public class WareRabbitInitializeConfig {

    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false);
    }

    @RabbitListener
    public void listener(Message message){

    }

    //String name, boolean durable, boolean exclusive, boolean autoDelete,@Nullable Map<String, Object> arguments
    @Bean
    public Queue stockReleaseStockQueue() {
        //exclusive:排他性  很多人都可以连
        return new Queue("stock.release.stock.queue", true, false, false);
    }

    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        //设置50分钟
        arguments.put("x-message-ttl", 2 * 60 * 1000);
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    @Bean
    public Binding releaseBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }

    @Bean
    public Binding lockedBinding(){
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }

}
