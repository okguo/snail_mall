package com.okguo.snailmall.member.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/08 10:00
 */
@FeignClient("snailmall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/memberCoupons")
    public R getCouponList();

}
