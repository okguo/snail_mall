package com.okguo.snailmall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.okguo.common.exception.BizCodeEnum;
import com.okguo.common.exception.RRException;
import com.okguo.common.to.SkuHasStockVo;
import com.okguo.snailmall.ware.vo.LockStockResult;
import com.okguo.snailmall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.okguo.snailmall.ware.entity.WareSkuEntity;
import com.okguo.snailmall.ware.service.WareSkuService;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.R;


/**
 * 商品库存
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 14:01:28
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 锁库存
     */
    @PostMapping("/orderLock")
    public R orderLock(@RequestBody WareSkuLockVo wareSkuLockVo) {
        try {
            Boolean results = wareSkuService.orderLock(wareSkuLockVo);
            return R.ok();
        } catch (RRException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(),BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }

    }

    /**
     * 列表
     */
    @PostMapping("/hasStock")
//    @RequiresPermissions("ware:waresku:list")
    public R hasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> stockVos = wareSkuService.queryHasStock(skuIds);
        return R.ok(stockVos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
