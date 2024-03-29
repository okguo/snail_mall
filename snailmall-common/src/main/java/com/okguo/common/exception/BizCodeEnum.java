package com.okguo.common.exception;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/25 21:15
 * <p>
 * 通用错误码表
 * 10：通用
 * 002：短信验证码获取频率过高
 * 11：商品
 * 12：订单
 * 13：购物车
 * 14：物流
 * 15: 用户
 * 001：用户手机号重复
 * 002：用户名称重复
 * 16: 库存
 */
public enum BizCodeEnum {
    TOO_MANY_REQUEST(10000, "请求流量过大"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    MOBILE_EXIST_EXCEPTION(15001, "用户手机号已存在"),
    USERNAME_EXIST_EXCEPTION(15002, "用户名已存在"),
    USER_ACCOUNT_ERROR_EXCEPTION(15003, "账号或密码错误"),
    NO_STOCK_EXCEPTION(16001, "商品库存不足"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率过高，请稍后再试");

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
