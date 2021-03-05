package com.okguo.common.exception;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/25 21:15
 *
 * 通用错误码表
 * 10：通用
 *     002：短信验证码获取频率过高
 * 11：商品
 * 12：订单
 * 13：购物车
 * 14：物流
 */
public enum BizCodeEnum {
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    SMS_CODE_EXCEPTION(10002,"验证码获取频率过高，请稍后再试");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
