package com.okguo.seckill.service;

import com.okguo.seckill.vo.SeckillSkuRelationVo;

import java.util.List;

public interface SeckillService {
    void shelfSeckillSku();

    List<SeckillSkuRelationVo> queryCurrentSeckillSkus();

}
