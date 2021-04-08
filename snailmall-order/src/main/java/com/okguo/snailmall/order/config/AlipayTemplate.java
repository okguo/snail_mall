package com.okguo.snailmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.okguo.snailmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2021000117632125";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCiFMGC9Yum0W4QzxZbPFMVSOjt+bIK4mqqpiDdAKss9a2zq+RjQk/oAjWdZUQzNJzTwpSVr56PD++TnRgy6EsUHPwqbdNQW1z0wCPY5nvTAThGaYRtaUKvX50htYO4O5CILmmodr5KkFewkE9CUxEcV3T1ofVjhYkvNQSRJFFH2dn7XWkx0sDLB6h/noXSTPE7kCerrlBlhTr03k2WUpzoWEd+gAoD1Eslh+Oqi24MR0bMrylijC386Yi2zKdZXtzr2mSGWpPZJ+Lwz9PhMx+V9zphLR2LOKpVkQLIn00qlhY1SK87rvsdSyjLS+Hlh86QM0JxP+BKG/50RQNmnMITAgMBAAECggEAap3IpUKsOHGvOzNd+2n1JR0uy8viiDJkBBj/qSDVeN+rAXNi6sAmG/HIHt8MO7qyJsHqR4ScgnLzH2DJGEOhj3t5jNdtl4Bh50W0zureLPCyee7TA72n9gCpNhFWsgvMiSgurHhfuGC2rhhfaruFl+UXtLRy3KJsmTcA6BDBvsbMtAAZYmdZ3zlRwKWDjLg97K7kRQ39fiIIjyj5+8FoMMRkYxX6m0qjiufcU5pHhKbsKojcvTpvR87IgetEAANsxo1gdgtdNvjz/TZYE040sZPWKHAvDFa2l9No8JZsKuXmEJ/17lh0rYv7D/EOAsnmx3OaDujVo6CwDfJSAGSpwQKBgQDW5T00e5Bs31sJEaLGktz3+Y6IfdgtBCSLWoNHZIs+qrpei5L3JPHFw2MTgCQ2kIDx+arGxvASPVFRvBrx4pEVCx6EZ9a10DCe0HGGAiaU6p8BA+4UDuKBpnwGt5LWaG6CFixSNHlaN+6mwTrYeyXZDdFNoskvplx+0lVCAj2HowKBgQDBFVxmb68amco8uTPEjiUTdsf0jF9ki+kbuGsA2Ht3EtiDm+p8h3c5ESw3gc+zSp1pX7+o3ocGQBZLeV8MWHx+H9VVNsev5HfWH9sPZwjJ5s6+ABuizzZ9ifq+pA+bUZBoV169Bl0r5kuy74uglO/gAS/Q8rGeT7IISvM8T4NC0QKBgFOheTVQlT9ZmVMpMuD2zNoQnADQgO3b++Djw81n+qzTwp4C0ZovHYxqYsd3CiDWEeiZ0nVzfICMrnYRr6bNFstKvYCn7K5rLFDSLCwL8DnqKSE7B091D3+HbCMtFA8vKzqjKHKBi2yp8c2hy6XuHyeesgL1xDgwFTUh5B5BQvLjAoGAdoKG6TTJlBkqwSSnepAIf67bQeVas5sQY/xIaQd2mHpPvE/Zl7+J1NQOgK9DbXdxsHidaafT1mlFheOkDC7PkpY1WY06+WHQj/OTgBuskEgDQylLzR1EA7/J0HNgROPGL9TYiEq8qzI56kUPPWvM43+2+pfLnzCpRF/EdZMULNECgYAeacrH/dMjWFOzMxfRoh0oIDjeXtR/xxErzL3RXTmmJcELQ4pBbs57bDMkG8VTq9AF+hvOJW2Vn82ZWj2qvZKadgJyHtJQZnw6uL6TrHCrLXutba4elwQ0WAvPXRFVYlnOyjViOYUCDky2U6ox4XfVuqv+fSVhds60kMF+vKNhlQ==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA9B2FAIN+8vgbwGpLR+VEsUJmccqF41QmZICdUOIb8/Mst5DdxZdOINN9v/1pmjI9sINGtyN+vJlNs8CI/HH+15Zcwuai8dmWTKgFwieRI0IjupDaJkYF2+dLLuEbPdaXSb9Gc9ILk0fhsnjmP5liQLkxL6CPxdyUQvhjevQKVJdWpjDNJEpThCsdnzL7l9PuJGj1oYylmX5MlyoTiQBjXk+5hKVN2D7AzJfldXbM8H36DITenUyZ17hu4ZUgOSPtXFRBlxueAEsvmwjnVhcCZse6vHLK8D0BmYhqi2xejq8VdU+VsUKwWO9L0SCnCgg3B5+yndTGeSHL1GBRHlgyKwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "http://twrhy7wthd.52http.tech/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://member.snailmall.com/orderList.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    private String timeout = "30m";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
