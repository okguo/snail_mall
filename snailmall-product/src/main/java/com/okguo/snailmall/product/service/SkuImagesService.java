package com.okguo.snailmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 11:01:17
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuImagesEntity> queryBySkuId(Long skuId);
}

