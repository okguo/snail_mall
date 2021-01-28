package com.okguo.snailmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.product.entity.CategoryEntity;
import com.okguo.snailmall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 11:01:18
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * @Description: 查询商品类别树形结构
     * @Author: Guoyongfu
     * @Date: 2021/1/11 20:37
     */
    List<CategoryEntity> queryWithTree();

    /**
     * @Description: 批量删除
     * @Author: Guoyongfu
     * @Date: 2021/1/13 12:22
     */
    void removeByCateIds(List<Long> asList);

    /**
     * @Description: 新增类别
     * @Author: Guoyongfu
     * @Date: 2021/1/15 19:37
     */
    boolean save(CategoryEntity category);

    Long[] queryCategoryPathById(Long categoryId);

    void updateDetail(CategoryEntity category);

    List<CategoryEntity> queryLevelOneCategory();

    Map<String, List<Catelog2Vo>> queryCatalogJson();

}

