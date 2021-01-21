package com.okguo.snailmall.product.feign;

import com.okguo.common.to.SkuReductionTo;
import com.okguo.common.to.SpuBoundTo;
import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/20 17:50
 */
@FeignClient(value = "snailmall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);


}
