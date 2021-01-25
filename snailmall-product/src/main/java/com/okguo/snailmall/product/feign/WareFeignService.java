package com.okguo.snailmall.product.feign;

import com.okguo.common.to.SkuHasStockVo;
import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/25 19:21
 */
@FeignClient("snailmall-ware")
public interface WareFeignService {


    @PostMapping("ware/waresku/hasStock")
    R queryStockBySkuIds(@RequestBody List<Long> skuIds);

}
