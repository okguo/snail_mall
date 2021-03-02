package com.okguo.snailmall.search;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/02 16:12
 */
public class Test {

    public static void main(String[] args) {
        String str = "http://search.snailmall.com/list.html&catalog3Id=225";
        System.out.println(str.charAt(str.indexOf("catalog3Id=225")-1));
    }
}
