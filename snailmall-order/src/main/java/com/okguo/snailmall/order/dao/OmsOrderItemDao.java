package com.okguo.snailmall.order.dao;

import com.okguo.snailmall.order.entity.OmsOrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 15:20:04
 */
@Mapper
public interface OmsOrderItemDao extends BaseMapper<OmsOrderItemEntity> {
	
}
