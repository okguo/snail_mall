package com.okguo.snailmall.order.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/22 19:35
 */
@FeignClient("snailmall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    R hasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fee")
    R getFee(@RequestParam("addrId") Long addrId);

}
