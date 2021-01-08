package com.okguo.snailmall.coupon.dao;

import com.okguo.snailmall.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:51:20
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
