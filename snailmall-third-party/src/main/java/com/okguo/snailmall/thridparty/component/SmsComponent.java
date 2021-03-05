package com.okguo.snailmall.thridparty.component;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.okguo.snailmall.thridparty.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/05 15:41
 */
@ConfigurationProperties(prefix = "spring.alicloud.sms")
@Data
@Component
public class SmsComponent {

    private String smsHost;
    private String appCode;
    private String templateId;

    public boolean sendSmsCode(String mobile, String code) {
        boolean result = false;

        String path = "/sms/send";
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appCode);
        Map<String, String> querys = new HashMap<>();
        querys.put("receive", mobile);
        querys.put("tag", code);
        querys.put("templateId", templateId);
        Map<String, String> bodys = new HashMap<>();

        try {
            HttpResponse response = HttpUtils.doPost(smsHost, path, method, headers, querys, bodys);
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            if (200 == (int) jsonObject.get("code")) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
