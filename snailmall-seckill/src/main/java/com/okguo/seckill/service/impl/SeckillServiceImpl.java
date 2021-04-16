package com.okguo.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.okguo.common.utils.R;
import com.okguo.seckill.feign.CouponFeignService;
import com.okguo.seckill.feign.ProductFeignService;
import com.okguo.seckill.service.SeckillService;
import com.okguo.seckill.vo.Latest3DaySessionVo;
import com.okguo.seckill.vo.SeckillSkuRelationVo;
import com.okguo.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
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
