package com.okguo.seckill.controller;

import com.okguo.common.utils.R;
import com.okguo.seckill.service.SeckillService;
import com.okguo.seckill.vo.SeckillSkuRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R queryCurrentSeckillSkus(){
        List<SeckillSkuRelationVo> skuRelationVos = seckillService.queryCurrentSeckillSkus();
        return R.ok(skuRelationVos);
    }

    @ResponseBody
    @GetMapping("/querySkuSeckillInfo/{skuId}")
    public R querySkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SeckillSkuRelationVo vo = seckillService.querySkuSeckillInfo(skuId);
        return R.ok(vo);
    }

    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId, @RequestParam("key") String key, @RequestParam("num") Integer num, Model model){
        String orderSn = seckillService.seckill(killId,key,num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }
}
