package com.okguo.snailmall.order;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/18 16:23
 */
@FeignClient("snailmall-member")
public interface MemberFeignService {
}
