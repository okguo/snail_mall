package com.okguo.snailmall.order.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/23 16:37
 */
@FeignClient("snailmall-product")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/skuId/{skuId}")
    R querySpuBySkuId(@PathVariable("skuId") Long skuId);

}
