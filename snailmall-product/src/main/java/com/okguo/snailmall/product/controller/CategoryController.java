package com.okguo.snailmall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.okguo.snailmall.product.entity.CategoryEntity;
import com.okguo.snailmall.product.service.CategoryService;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.R;

import javax.validation.Valid;


/**
 * 商品三级分类
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-07 11:38:36
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询商品分类树形结构
     */
    @RequestMapping("/list/tree")
    public R listWithTree() {
        return R.ok().put("data", categoryService.queryWithTree());
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryService.queryPage(params);

        return R.ok(page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
//    @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok(category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:category:save")
    public R save(@RequestBody @Valid CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("product:category:update")
    public R update(@RequestBody @Valid CategoryEntity category) {
        categoryService.updateDetail(category);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds) {
        categoryService.removeByCateIds(Arrays.asList(catIds));

        return R.ok();
    }

}
