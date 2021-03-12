package com.okguo.snailmall.cart.vo;

import lombok.Data;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/11 16:12
 */
@Data
public class UserInfoTo {

    private Long userId;
    private String userKey; //一定封装

    private boolean tempUser = false;
}
