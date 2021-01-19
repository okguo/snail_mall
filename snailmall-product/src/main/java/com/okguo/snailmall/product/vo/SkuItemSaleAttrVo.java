package com.okguo.snailmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<com.atguigu.gulimall.product.vo.AttrValueWithSkuIdVo> attrValues;
}
