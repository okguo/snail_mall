package com.okguo.snailmall.search.controller;

import com.okguo.snailmall.search.service.MallSearchService;
import com.okguo.snailmall.search.vo.SearchParam;
import com.okguo.snailmall.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/02/02 13:22
 */
@Controller
public class SearchController {

    @Resource
    private MallSearchService mallSearchService;

    @RequestMapping("list.html")
    public String getList(SearchParam param, Model model){

        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }

}
