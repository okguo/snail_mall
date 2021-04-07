package com.okguo.snailmall.member.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("snailmall-order")
public interface OrderFeignService {

    @PostMapping("/order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);

}
