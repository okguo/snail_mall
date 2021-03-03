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
 * @Date: 2021/03/03 17:02
 */
@Slf4j
@SpringBootTest
class AttrGroupServiceTest {
    @Autowired
    private AttrGroupService attrGroupService;
    @Test
    void getAttrGroupWithAttrsBySpuIdAndCatelgoryId() {
        log.info(JSON.toJSONString(attrGroupService.getAttrGroupWithAttrsBySpuIdAndCatelgoryId(16L,225L)));

    }
}