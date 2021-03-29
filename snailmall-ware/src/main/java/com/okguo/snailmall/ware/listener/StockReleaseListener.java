package com.okguo.snailmall.ware.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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

@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    void handleStockLockRelease(String stockLockedToString, Message message, Channel channel) throws IOException {
        StockLockedTo stockLockedTo = JSON.parseObject(stockLockedToString, StockLockedTo.class);
        wareSkuService.unlockStock();
        Long taskId = stockLockedTo.getId();
        Long skuId = stockLockedTo.getDetail().getSkuId();
        Long detailId = stockLockedTo.getDetail().getId();

        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailId);
        if (detailEntity == null) {
            //无需释放库存，因为已经被事务会滚掉了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(taskId);
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
                });
                //如果是取消状态才能解锁库存
                if (orderVo == null || orderVo.getStatus() == 4) {
                    wareSkuService.unlockStock(skuId, stockLockedTo.getDetail().getWareId(), stockLockedTo.getDetail().getSkuNum());
                    //通知消息队列消息成功，否则消息队列中不会自动删除
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }
            } else {
                //消息拒绝后重新放入队列中，让别人继续消费解锁
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
        }
    }

}
