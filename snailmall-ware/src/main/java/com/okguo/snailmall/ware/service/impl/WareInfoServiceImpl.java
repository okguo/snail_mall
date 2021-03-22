package com.okguo.snailmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.okguo.common.utils.R;
import com.okguo.snailmall.ware.feign.MemberFeignService;
import com.okguo.snailmall.ware.vo.FareVo;
import com.okguo.snailmall.ware.vo.MemberAddressVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.ware.dao.WareInfoDao;
import com.okguo.snailmall.ware.entity.WareInfoEntity;
import com.okguo.snailmall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");

        if (StringUtils.isNotEmpty(key)) {
            wareInfoEntityQueryWrapper.like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFee(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo memberAddressVo = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        //TODO
        if (memberAddressVo != null) {
            String phone = memberAddressVo.getPhone();
            String substring = phone.substring(phone.length() - 1);
            fareVo.setFare(new BigDecimal(substring));
            fareVo.setAddress(memberAddressVo);
            return fareVo;
        }
        return null;
    }

}