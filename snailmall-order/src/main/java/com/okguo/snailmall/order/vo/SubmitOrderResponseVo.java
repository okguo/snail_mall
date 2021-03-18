package com.okguo.snailmall.order.vo;

import com.okguo.snailmall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;
    private Integer code;//0成功   错误状态码
}
