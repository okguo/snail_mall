package com.okguo.snailmall.product.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/01 17:18
 */
@Slf4j
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Test
    void queryCategoryPathById() {
        log.info("CategoryServiceTest->queryCategoryPathById:"+ JSON.toJSONString(categoryService.queryCategoryPathById(225L)));
    }
}