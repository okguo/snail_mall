package com.okguo.snailmall.product.feign;

import com.okguo.common.to.es.SkuEsModel;
import com.okguo.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/25 21:22
 */
@FeignClient("snailmall-elasticsearch")
public interface ElasticSearchFeignService {

    @PostMapping("search/save/product")
    public R saveProduct(@RequestBody List<SkuEsModel> skuEsModels);

}
