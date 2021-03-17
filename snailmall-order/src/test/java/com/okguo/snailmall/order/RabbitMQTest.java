package com.okguo.snailmall.order;

import com.alibaba.fastjson.JSON;
import com.okguo.snailmall.order.entity.OrderReturnReasonEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/16 19:30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQTest {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate template;


    @Test
    public void sendMessage() {
        OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
        entity.setId(2L);
        entity.setCreateTime(new Date());
        entity.setName("张三哈哈哈哈哈");
        entity.setStatus(22);
        template.convertAndSend("javaTestDirectExchange","test_routing_key",entity);
    }


    @Test
    public void createExchange(){
        DirectExchange directExchange = new DirectExchange("javaTestDirectExchange", true, false);
        amqpAdmin.declareExchange(directExchange);
    }

    @Test
    public void createQueue(){
        System.out.println(JSON.toJSONString(amqpAdmin.declareQueue(new Queue("javaTestQueue",true,false,false))));
    }

    @Test
    public void createConnection(){
        Binding binding = new Binding("javaTestQueue", Binding.DestinationType.QUEUE, "javaTestDirectExchange", "test_routing_key", new HashMap<>());
        amqpAdmin.declareBinding(binding);

    }


}
