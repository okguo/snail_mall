package com.okguo.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.okguo.common.to.mq.SeckillOrderTo;
import com.okguo.common.utils.R;
import com.okguo.common.vo.MemberVO;
import com.okguo.seckill.feign.CouponFeignService;
import com.okguo.seckill.feign.ProductFeignService;
import com.okguo.seckill.interceptor.LoginUserInterceptor;
import com.okguo.seckill.service.SeckillService;
import com.okguo.seckill.vo.Latest3DaySessionVo;
import com.okguo.seckill.vo.SeckillSkuRelationVo;
import com.okguo.seckill.vo.SkuInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:session:";
    private final String SECKILL_CACHE_PREFIX = "seckill:skus:";
    private final String SKU_STOCK_SEMAPHORE = "seckill:sku:";

    @Override
    public void shelfSeckillSku() {
        R r = couponFeignService.getLatest3DaySession();
        if (r.getCode() == 0) {
            List<Latest3DaySessionVo> sessionData = r.getData(new TypeReference<List<Latest3DaySessionVo>>() {
            });
            //1.缓存活动信息
            saveSessionInfos(sessionData);
            //2.缓存商品信息
            saveSessionSkuInfos(sessionData);
        }
    }

    @Override
    public List<SeckillSkuRelationVo> queryCurrentSeckillSkus() {
        //1.确定当前时间数据哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        assert keys != null;
        for (String key : keys) {
            String replace = key.replace(SESSION_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);
            if (time >= start && time <= end) {
                //2.获取当前秒杀场次的所有商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
                assert range != null;
                List<String> list = hashOps.multiGet(range);
                if (list != null && list.size() > 0) {
                    return list.stream().map(e -> JSON.parseObject(e, SeckillSkuRelationVo.class)).collect(Collectors.toList());
                }
                break;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public SeckillSkuRelationVo querySkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                if (Long.parseLong(key.split("_")[1]) == skuId) {
                    SeckillSkuRelationVo seckillSkuRelationVo = JSON.parseObject(hashOps.get(key), SeckillSkuRelationVo.class);
                    long time = new Date().getTime();
                    assert seckillSkuRelationVo != null;
                    if (time < seckillSkuRelationVo.getStartTime() || time > seckillSkuRelationVo.getEndTime()) {
                        seckillSkuRelationVo.setRandomCode(null);
                    }
                    return seckillSkuRelationVo;
                }
            }
        }
        return null;
    }

    //秒杀业务处理
    @Override
    public String seckill(String killId, String key, Integer num) {
        MemberVO memberVO = LoginUserInterceptor.threadLocal.get();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
        String s = hashOps.get(killId);
        //校验参数
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        //1.校验时间合法性
        SeckillSkuRelationVo seckillSkuRelationVo = JSONObject.parseObject(s, SeckillSkuRelationVo.class);
        long startTime = seckillSkuRelationVo.getStartTime();
        long endTime = seckillSkuRelationVo.getEndTime();
        long currentTime = new Date().getTime();
        if (currentTime < startTime || currentTime > endTime) {
            return null;
        }
        //2.校验随机码 和 商品id
        String randomCode = seckillSkuRelationVo.getRandomCode();
        String skuId = seckillSkuRelationVo.getPromotionSessionId() + "_" + seckillSkuRelationVo.getSkuId();
        if (!StringUtils.equals(key, randomCode) || !StringUtils.equals(killId, skuId)) {
            return null;
        }
        //3.校验数量合法性
        if (num > seckillSkuRelationVo.getSeckillLimit()) {
            return null;
        }
        //4.校验当前用户是否已经购买过
        Long id = memberVO.getId();
        //SETNX
        String redisKey = memberVO.getId() + "_" + skuId;
        //过期时间
        long ttl = endTime - currentTime;
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
        if (!aBoolean) {
            //占位失败，说明已买过
            return null;
        }
        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
        try {
            boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
            if (!b) {
                return null;
            }
            String timeId = IdWorker.getTimeId();
            SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
            seckillOrderTo.setOrderSn(timeId);
            seckillOrderTo.setPromotionSessionId(seckillSkuRelationVo.getPromotionSessionId());
            seckillOrderTo.setNum(num);
            seckillOrderTo.setSkuId(seckillSkuRelationVo.getSkuId());
            seckillOrderTo.setSeckillPrice(seckillSkuRelationVo.getSeckillPrice());
            seckillOrderTo.setMemberId(memberVO.getId());
            //发送消息到队列
            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", JSON.toJSONString(seckillOrderTo));

            //返回订单号
            return timeId;

        } catch (InterruptedException e) {
            //秒杀失败
            return null;
        }
    }

    private void saveSessionInfos(List<Latest3DaySessionVo> sessionData) {
        sessionData.forEach(session -> {
            long start = session.getStartTime().getTime();
            long end = session.getEndTime().getTime();
            String key = SESSION_CACHE_PREFIX + start + "_" + end;

            boolean aBoolean = redisTemplate.hasKey(key);
            if (!aBoolean) {
                List<String> collect = session.getSkuRelationEntities().stream().map(e -> e.getPromotionSessionId() + "_" + e.getSkuId()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }

    private void saveSessionSkuInfos(List<Latest3DaySessionVo> sessionData) {
        sessionData.forEach(session -> {
            BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
            List<SeckillSkuRelationVo> skuRelationVos = session.getSkuRelationEntities();
            skuRelationVos.forEach(skuRelationVo -> {
                String token = UUID.randomUUID().toString().replace("-", "");
                if (!boundHashOps.hasKey(skuRelationVo.getPromotionSessionId() + "_" + skuRelationVo.getSkuId())) {
                    //0.秒杀基本信息
                    //1.sku基本信息
                    R r = productFeignService.querySkuInfo(skuRelationVo.getSkuId());
                    SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    skuRelationVo.setSkuInfoVo(skuInfo);
                    //2.开始时间结束时间信息
                    skuRelationVo.setStartTime(session.getStartTime().getTime());
                    skuRelationVo.setEndTime(session.getEndTime().getTime());
                    //3.随机码  一个商品一个随机码 防止攻击
                    skuRelationVo.setRandomCode(token);
                    boundHashOps.put(skuRelationVo.getPromotionSessionId().toString() + "_" + skuRelationVo.getSkuId(), JSON.toJSONString(skuRelationVo));

                    //4.引入分布式信号量，设置库存数为信号总数  作用：限流
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(skuRelationVo.getSeckillCount());
                }
            });
        });
    }
}
