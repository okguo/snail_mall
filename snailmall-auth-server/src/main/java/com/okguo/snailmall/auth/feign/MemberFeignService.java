package com.okguo.snailmall.auth.feign;

import com.okguo.common.utils.R;
import com.okguo.snailmall.auth.vo.UserRegisterVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/08 16:15
 */
@FeignClient("snailmall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    public R register(@RequestBody UserRegisterVO param);
}
