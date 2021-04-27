package com.okguo.snailmall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;

/**
 * 网关sentinel配置
 *
 * @author guoyongfu
 */
@Configuration
public class SentinelGatewayConfig {
    /**
     * GatewayCallbackManager
     */
    public SentinelGatewayConfig(){
        GatewayCallbackManager.resetBlockHandler();
    }

}
