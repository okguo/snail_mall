package com.okguo.common.to.mq;

import lombok.Data;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/29 19:52
 */
@Data
public class WareOrderTaskDetailTo {

    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;

}
