package com.okguo.snailmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/22 19:39
 */
@Configuration
public class SnailmallElasticSearchConfig {


    @Bean
    public RestHighLevelClient getEsClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.56.10", 9200, "http"));
        return new RestHighLevelClient(builder);
    }


}
