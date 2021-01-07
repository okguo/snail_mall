package com.okguo.snailmall.order.dao;

import com.okguo.snailmall.order.entity.OmsPaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 15:20:04
 */
@Mapper
public interface OmsPaymentInfoDao extends BaseMapper<OmsPaymentInfoEntity> {
	
}
