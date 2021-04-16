package com.okguo.seckill.task;

import com.okguo.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架
 * 每天凌晨3点上架近3天需要秒杀的商品
 * 当天 00:00:00 - 23:59:59
 * 明天 00:00:00 - 23:59:59
 * 后天 00:00:00 - 23:59:59
 */

@Slf4j
@Service
public class SeckillTask {

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedissonClient redissonClient;

    private final String SECKILL_SHELF_LOCK = "seckill:shelf:lock";

    @Scheduled(cron = "0 * * * * ?")
    public void shelfSeckillSkuLatest3Days() {
        log.info("定时上架秒杀商品开始。。。");
        RLock rLock = redissonClient.getLock(SECKILL_SHELF_LOCK);
        rLock.lock(10, TimeUnit.MILLISECONDS);
        try {
            seckillService.shelfSeckillSku();
        } finally {
            if(rLock.isLocked()){ // 是否还是锁定状态
                if(rLock.isHeldByCurrentThread()){ // 时候是当前执行线程的锁
                    rLock.unlock(); // 释放锁
                }
            }
        }
        log.info("定时上架秒杀商品结束。。。");
    }


}
