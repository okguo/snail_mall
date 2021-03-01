package com.okguo.snailmall.search.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/01 16:22
 */
@FeignClient("snailmall-product")
public interface ProductFeignService {

    @GetMapping("product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);

}
