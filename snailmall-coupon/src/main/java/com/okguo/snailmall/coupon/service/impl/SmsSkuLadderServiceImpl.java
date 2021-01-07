package com.okguo.snailmall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.coupon.dao.SmsSkuLadderDao;
import com.okguo.snailmall.coupon.entity.SmsSkuLadderEntity;
import com.okguo.snailmall.coupon.service.SmsSkuLadderService;


@Service("smsSkuLadderService")
public class SmsSkuLadderServiceImpl extends ServiceImpl<SmsSkuLadderDao, SmsSkuLadderEntity> implements SmsSkuLadderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsSkuLadderEntity> page = this.page(
                new Query<SmsSkuLadderEntity>().getPage(params),
                new QueryWrapper<SmsSkuLadderEntity>()
        );

        return new PageUtils(page);
    }

}