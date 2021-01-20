package com.okguo.snailmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.product.entity.AttrAttrgroupRelationEntity;
import com.okguo.snailmall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 11:01:18
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatch(List<AttrGroupRelationVo> vos);
}

