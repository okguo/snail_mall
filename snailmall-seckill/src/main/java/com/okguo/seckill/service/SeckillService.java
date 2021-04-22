package com.okguo.seckill.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.okguo.seckill.vo.SeckillSkuRelationVo;

import java.util.List;

public interface SeckillService {
    void shelfSeckillSku();

    @SentinelResource
    List<SeckillSkuRelationVo> queryCurrentSeckillSkus();

    SeckillSkuRelationVo querySkuSeckillInfo(Long skuId);

    String seckill(String killId, String key, Integer num);
}
