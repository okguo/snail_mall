package com.okguo.snailmall.order.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/18 16:23
 */
@FeignClient("snailmall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/listByMemberId")
    R queryMemberAddressByMemberId(@RequestParam("memberId") Long memberId);
}
