package com.okguo.seckill.controller;

import com.okguo.common.utils.R;
import com.okguo.seckill.service.SeckillService;
import com.okguo.seckill.vo.SeckillSkuRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @GetMapping("/currentSeckillSkus")
    public R queryCurrentSeckillSkus(){
        List<SeckillSkuRelationVo> skuRelationVos = seckillService.queryCurrentSeckillSkus();
        return R.ok(skuRelationVos);
    }

    @GetMapping("/querySkuSeckillInfo/{skuId}")
    public R querySkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SeckillSkuRelationVo vo = seckillService.querySkuSeckillInfo(skuId);
        return R.ok(vo);
    }
}
