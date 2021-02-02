package com.okguo.snailmall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/02/02 13:22
 */
@Controller
public class SearchController {

    @RequestMapping("list.html")
    public String getList(){
        return "list";
    }

}
