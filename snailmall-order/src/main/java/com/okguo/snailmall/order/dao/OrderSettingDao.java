package com.okguo.snailmall.order.dao;

import com.okguo.snailmall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:58:41
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
