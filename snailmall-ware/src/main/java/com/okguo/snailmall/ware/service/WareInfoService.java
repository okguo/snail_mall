package com.okguo.snailmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.ware.entity.WareInfoEntity;
import com.okguo.snailmall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 14:01:28
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFee(Long addrId);
}

