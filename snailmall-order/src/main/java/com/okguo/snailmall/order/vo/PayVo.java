package com.okguo.snailmall.order.vo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

@Data
public class PayVo {
    private String out_trade_no; // 商户订单号 必填
    private String subject; // 订单名称 必填
    private String total_amount;  // 付款金额 必填
    private String body; // 商品描述 可空


    public volatile Double banlace;


    public static void main(String[] args) {
        Runnable runnable[] = new Runnable[100];
        for (int i = 0; i < 100; i++) {
            Runnable runnable1 = new Runnable() {
                @Override
                public void run() {

                }
            };
        }


    }

    static class ThreadTest extends Thread{

        private Object atomicReference;

        @Override
        public void run(){
        }

    }

}
