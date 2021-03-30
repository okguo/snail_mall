package com.okguo.snailmall.ware.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.okguo.common.to.OrderTo;
import com.okguo.common.to.mq.StockLockedTo;
import com.okguo.common.utils.R;
import com.okguo.snailmall.ware.entity.WareOrderTaskDetailEntity;
import com.okguo.snailmall.ware.entity.WareOrderTaskEntity;
import com.okguo.snailmall.ware.feign.OrderFeignService;
import com.okguo.snailmall.ware.service.WareOrderTaskDetailService;
import com.okguo.snailmall.ware.service.WareOrderTaskService;
import com.okguo.snailmall.ware.service.WareSkuService;
import com.okguo.snailmall.ware.vo.OrderVo;
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
 * @Date: 2021/03/29 20:55
 */
@Slf4j
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    void handleStockLockRelease(String stockLockedToString, Message message, Channel channel) throws IOException {
        log.info("收到库存解锁的消息！！！消息体为：" + stockLockedToString);

        StockLockedTo stockLockedTo = JSON.parseObject(stockLockedToString, StockLockedTo.class);
        try {
            wareSkuService.unlockStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    void handleStockLockRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        log.info("收到订单关闭消息，开始检查库存是否释放！！！消息体为：" + JSON.toJSONString(orderTo));
        try {
            wareSkuService.checkWareRelease(orderTo.getOrderSn());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
