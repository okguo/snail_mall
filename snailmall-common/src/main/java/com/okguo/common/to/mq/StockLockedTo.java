package com.okguo.common.to.mq;

import lombok.Data;


/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/29 19:32
 */
@Data
public class StockLockedTo {
    private Long id; //工作单id
    private WareOrderTaskDetailTo detail; //工作单明细

}
