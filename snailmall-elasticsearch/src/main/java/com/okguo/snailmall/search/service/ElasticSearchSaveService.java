package com.okguo.snailmall.search.service;

import com.okguo.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/25 20:45
 */
public interface ElasticSearchSaveService {
    boolean saveProduct(List<SkuEsModel> skuEsModels) throws IOException;
}
