package com.okguo.snailmall.search.controller;

import com.okguo.common.exception.BizCodeEnum;
import com.okguo.common.to.es.SkuEsModel;
import com.okguo.common.utils.R;
import com.okguo.snailmall.search.service.ElasticSearchSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/25 20:41
 */
@Slf4j
@RequestMapping("search/save")
@RestController
public class ElasticSearchSaveController {

    @Autowired
    private ElasticSearchSaveService searchSaveService;

    @PostMapping("/product")
    public R saveProduct(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean result = false;
        try {
            result = searchSaveService.saveProduct(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSearchSaveController==>商品上架错误：{}", e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (!result) {
            return R.ok();
        }
        return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }


}
