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
    private   String app_id = "2021000117632125";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCCaIqNV4zpYCSQUmVwZzlM9k1I0JH16gINDL/9vYWDMBCwTxbBGKNN45cae0jdfRSWDlHsz5ryyNSQt/UM3kE7tyJnurdE6s2Fmhw03Z8XE7fhkjXhqxBWHfR+Bip5ftucepgzy1XtaEC6cpF8NmnzeI/hjziBx6NJl4MPmNy8ooWCRDiVmQfHsKOJcQ9EjutBD5rFOffuSYqNx821iIJk7or+Jzz/dYvI77rR9lMyjBMZKhZDWDP7Cjs8w9+LqMFenBqnA+Z/W+Ws69KZIZsHHKon5O88SR2F5qergU6YleuPcMO6kaKS8ZBhsjDEA/gA6+hIlOp8wp4JmH++y2MbAgMBAAECggEAIK5iSpmuTq4xe5mqoki8K3VuWNksOxyx9uNDGivs0bux8v1fDnVGN7bZn2FWGWvHRPPBxD8i7cU2wC3Z06e/YV+715MLatINP3Ynq3f+7qGaa4Vgy3U7WtmW9B5Hdrs7/jMbAZ/roNuHnTIpE5/vXXzRUg26c4u8eCzoQ3hjmUIkb1LEz0+8avGOVChFu0nk/ileDaDXNt1AebLaNR1YJNJqBxATbXJwCjI2wdoGnAnQGy6m5x5dOlkNWBRE01pWg2b0jr2yfUGWKeZhXxkc5oIJ+DGltLWDbI4AtT/D52qgQtD1vNRgG/leaV1Ju9BdO59Z6eEdd2ozrh09awpcwQKBgQDa4emPLTNBNCUHnkOm/mMEMeJ+d1B23xFiMulNXEddAQ3y8xYbZIdwtiZ7qbm/M29t8LOHvE6wjuBitOtCm/O+4qlwQ/1VWu6UopmiwgubRQYTcab3R6UkuiqP8+gVfd+8JZcs/3rXRGcwW2jQIh9g8ijz+smVy6zAu0PM042+PwKBgQCYhc5wg6KRqeW+frbK2lDO3hJcKgRc5v9uc0Hykf95ZrSYJRhzEexGRUCwsQP1ieQ32M1P9w5u9hqDWI+24QGLL4yKYD9IxdggmqXOfgO7LncYfDPGufvBZhXqhtXOoIhtTWZ8dFqNM/UTyv+iVGyd2pE9O0rHpx1sM8GgtYAcJQKBgDV5WcEj7tCwN5e735VNss0UjtwMtDjGWI3gGEVRFRIwrsK7Bpycm8Y6if5OQdXncAKw+ntf0u2pikIzJwK37P2mjHSj96FBvOXNL7GmrIU7gLeBXaQt+beWT82pbfrKLL3fhSZ2KAGtcXRL8Lhgjb/2iXDJxTP2dXcEiRTiXk+rAoGAczwtU+7wpdNSZFHCc5LsmM9J2d5Y+4P/mwhz2/yYo7WIXqcwSMhS1mi+ToD/d8Ud61/fBzqm6gkA0RnZQxfPHcToDyhWx/nKqB+PhGrLEOIzUwmL6OpdnqothssmwP2i9coT05XpUGPgF3s5FwFoKv+W2kdw4VWOslCMS4q15w0CgYBiato0WmWFTwo3PbHgcStkNzAjYOnzUvU6w66BPl3VKDoGraQza9JYeEZ7w10HpIIJ15U1MnL0hIqNDE11bHQVt/8D33QYf6VgBKQ87Dy2xZaNoeFN74FE+RqK1XmCKY3Geslt1FILkOlY2X9l38HL2Gk4bjHxsvFU8adDsolIGg==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgmiKjVeM6WAkkFJlcGc5TPZNSNCR9eoCDQy//b2FgzAQsE8WwRijTeOXGntI3X0Ulg5R7M+a8sjUkLf1DN5BO7ciZ7q3ROrNhZocNN2fFxO34ZI14asQVh30fgYqeX7bnHqYM8tV7WhAunKRfDZp83iP4Y84gcejSZeDD5jcvKKFgkQ4lZkHx7CjiXEPRI7rQQ+axTn37kmKjcfNtYiCZO6K/ic8/3WLyO+60fZTMowTGSoWQ1gz+wo7PMPfi6jBXpwapwPmf1vlrOvSmSGbBxyqJ+TvPEkdheanq4FOmJXrj3DDupGikvGQYbIwxAP4AOvoSJTqfMKeCZh/vstjGwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url="http://member.snailmall.com/orderList.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

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

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
