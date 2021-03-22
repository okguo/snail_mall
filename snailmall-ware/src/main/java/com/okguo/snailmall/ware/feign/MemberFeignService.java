package com.okguo.snailmall.ware.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/22 20:15
 */
@FeignClient("snailmall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
