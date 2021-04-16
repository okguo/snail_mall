package com.okguo.snailmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.okguo.common.utils.R;
import com.okguo.snailmall.product.entity.SkuImagesEntity;
import com.okguo.snailmall.product.entity.SpuInfoDescEntity;
import com.okguo.snailmall.product.feign.SeckillFeignService;
import com.okguo.snailmall.product.service.*;
import com.okguo.snailmall.product.vo.SeckillSkuInfoVo;
import com.okguo.snailmall.product.vo.SkuItemSaleAttrVo;
import com.okguo.snailmall.product.vo.SkuItemVo;
import com.okguo.snailmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.product.dao.SkuInfoDao;
import com.okguo.snailmall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private SeckillFeignService seckillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {

            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");

        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);

                if (bigDecimal.compareTo(new BigDecimal("0")) > 0) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> querySkuInfosBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVo queryInfo(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> future = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息获取  pms_sku_info
            SkuInfoEntity skuInfoEntity = baseMapper.selectById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrFuture = future.thenAcceptAsync((res) -> {
            //3、获取spu的销售属性组合。
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> descFuture = future.thenAcceptAsync((res) -> {
            //4、获取spu的介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.queryBySpuId(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, threadPoolExecutor);

        CompletableFuture<Void> baseFuture = future.thenAcceptAsync((res) -> {
            //5、获取spu的规格参数信息。
            List<SpuItemAttrGroupVo> itemAttrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuIdAndCatelgoryId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(itemAttrGroupVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //2、sku的图片信息  pms_sku_images
            List<SkuImagesEntity> imagesEntities = skuImagesService.queryBySkuId(skuId);
            skuItemVo.setImages(imagesEntities);
        }, threadPoolExecutor);

        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            //6、商品秒杀信息
            R r = seckillFeignService.querySkuSeckillInfo(skuId);
            if (r.getCode() == 0) {
                SeckillSkuInfoVo data = r.getData(new TypeReference<SeckillSkuInfoVo>() {
                });
                skuItemVo.setSeckillSkuInfoVo(data);
            }
        }, threadPoolExecutor);

        //异步编排
        CompletableFuture.allOf(saleAttrFuture, descFuture, baseFuture, imagesFuture, seckillFuture).get();

        return skuItemVo;
    }

}