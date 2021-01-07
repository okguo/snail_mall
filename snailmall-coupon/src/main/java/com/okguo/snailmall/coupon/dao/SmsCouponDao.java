package com.okguo.snailmall.coupon.dao;

import com.okguo.snailmall.coupon.entity.SmsCouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 15:14:38
 */
@Mapper
public interface SmsCouponDao extends BaseMapper<SmsCouponEntity> {
	
}
