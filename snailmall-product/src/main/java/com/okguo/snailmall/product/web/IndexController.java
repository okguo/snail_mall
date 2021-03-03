package com.okguo.snailmall.product.web;

import com.okguo.snailmall.product.entity.CategoryEntity;
import com.okguo.snailmall.product.service.CategoryService;
import com.okguo.snailmall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/26 15:31
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @RequestMapping({"*", "index.html", "index", "home"})
    public String getIndex(Model model) {

        List<CategoryEntity> categoryEntities = categoryService.queryLevelOneCategory();

        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    @ResponseBody
    @RequestMapping("index/json/catalog.json")
    public Map<String, List<Catelog2Vo>> queryCatalogJson() {
        return categoryService.queryCatalogJsonV3();
    }

    @RequestMapping("hello")
    public String hello() {

        RLock lock = redisson.getLock("my-lock");
        lock.lock();
        return "hello";
    }

}
