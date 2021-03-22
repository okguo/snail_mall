package com.okguo.snailmall.order.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/22 10:56
 */
@FeignClient("snailmall-cart")
public interface CartFeignService {

    @GetMapping("currentUserCartItems")
    R currentUserCartItems(@RequestParam("memberId") Long memberId);
}
