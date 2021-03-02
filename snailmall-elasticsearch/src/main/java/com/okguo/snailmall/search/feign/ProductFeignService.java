package com.okguo.snailmall.search.feign;

import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/01 16:22
 */
@FeignClient("snailmall-product")
public interface ProductFeignService {

    @GetMapping("product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);

    @RequestMapping("product/brand/infos")
//    @RequiresPermissions("product:brand:info")
    public R brandInfos(@RequestParam("brandIds") List<Long> brandIds);

    @RequestMapping("product/category/info/{catId}")
//    @RequiresPermissions("product:category:info")
    public R categoryInfo(@PathVariable("catId") Long catId);
}
