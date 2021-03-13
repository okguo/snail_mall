package com.okguo.snailmall.cart.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/13 15:25
 */
@FeignClient("snailmall-product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/skusaleattrvalue/getSkuSaleAttrStringList/{skuId}")
    R getSkuSaleAttrStringList(@PathVariable Long skuId);

}
