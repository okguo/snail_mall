package com.okguo.snailmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:51:19
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

