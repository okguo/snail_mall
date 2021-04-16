package com.okguo.snailmall.product.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("snailmall-seckill")
public interface SeckillFeignService {

    @GetMapping("/querySkuSeckillInfo/{skuId}")
    R querySkuSeckillInfo(@PathVariable("skuId") Long skuId);

}
