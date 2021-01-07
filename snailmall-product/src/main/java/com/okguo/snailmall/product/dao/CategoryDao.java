package com.okguo.snailmall.product.dao;

import com.okguo.snailmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 11:01:18
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
