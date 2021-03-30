package com.okguo.snailmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.to.SkuHasStockVo;
import com.okguo.common.to.mq.StockLockedTo;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.ware.entity.WareSkuEntity;
import com.okguo.snailmall.ware.vo.LockStockResult;
import com.okguo.snailmall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 14:01:28
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> queryHasStock(List<Long> skuIds);

    Boolean orderLock(WareSkuLockVo wareSkuLockVo);

    void unlockStock(StockLockedTo stockLockedTo);

    void unlockStockDB(Long skuId, Long wareId, Integer num, Long detailId);

    void checkWareRelease(String orderSn);
}

