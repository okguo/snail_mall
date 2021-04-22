package com.okguo.snailmall.member.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.fastjson.JSON;
import com.okguo.common.exception.BizCodeEnum;
import com.okguo.common.utils.R;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeckillSentinelConfig {

    public SeckillSentinelConfig(){
        WebCallbackManager.setUrlBlockHandler((request, response, e) -> {
            R error = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJSONString(error));
        });
    }

}
