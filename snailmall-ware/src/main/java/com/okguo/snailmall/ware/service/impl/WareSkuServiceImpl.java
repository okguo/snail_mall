package com.okguo.snailmall.ware.service.impl;

import com.okguo.common.exception.RRException;
import com.okguo.common.to.SkuHasStockVo;
import com.okguo.common.utils.R;
import com.okguo.snailmall.ware.feign.ProductFeignService;
import com.okguo.snailmall.ware.service.WareOrderTaskDetailService;
import com.okguo.snailmall.ware.service.WareOrderTaskService;
import com.okguo.snailmall.ware.vo.LockStockResult;
import com.okguo.snailmall.ware.vo.OrderItemVo;
import com.okguo.snailmall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.ware.dao.WareSkuDao;
import com.okguo.snailmall.ware.entity.WareSkuEntity;
import com.okguo.snailmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(skuId)) {
            wareSkuEntityQueryWrapper.eq("sku_id", skuId);
        }
        if (StringUtils.isNotEmpty(wareId)) {
            wareSkuEntityQueryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wareSkuEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");

                if (info.getCode() == 0) {
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {

            }


            wareSkuDao.insert(skuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> queryHasStock(List<Long> skuIds) {

        return skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = baseMapper.queryStockBySkuId(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count != null && count > 0);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 解锁库存的场景
     *  1.下单成功，未支付，超时自动解锁
     *  2.下单成功，订单异常，保证一致性，解锁库存
     */

    @Transactional(rollbackFor = RRException.class)
    @Override
    public Boolean orderLock(WareSkuLockVo wareSkuLockVo) {


        List<OrderItemVo> orderItemVos = wareSkuLockVo.getLocks();
        List<SkuWareHasStock> skuWareHasStocks = orderItemVos.stream().map(item -> {
            SkuWareHasStock hasStock = new SkuWareHasStock();
            hasStock.setSkuId(item.getSkuId());
            hasStock.setCount(item.getCount());
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(item.getSkuId());
            hasStock.setWareId(wareIds);
            return hasStock;
        }).collect(Collectors.toList());

        //锁定库存
        for (SkuWareHasStock skuWareHasStock : skuWareHasStocks) {
            boolean currentSkuLocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            Integer count = skuWareHasStock.getCount();
            List<Long> wareIds = skuWareHasStock.getWareId();
            if (wareIds == null || wareIds.size() < 1) {
                throw new RRException("商品id：" + skuId + ";库存不足");
            }
            for (Long wareId : wareIds) {
                Long result = wareSkuDao.lockSkuStock(skuId, wareId, count);
                if (result == 1) {
                    currentSkuLocked = true;

                    //发送消息
                    //TODO
//                    rabbitTemplate.convertAndSend()
                    break;
                }
            }
            if (!currentSkuLocked) {
                throw new RRException("商品id：" + skuId + ";库存不足");
            }
        }
        return true;
    }

    @Data
    static class SkuWareHasStock {
        private Long skuId;
        private Integer count;
        private List<Long> wareId;
    }

}