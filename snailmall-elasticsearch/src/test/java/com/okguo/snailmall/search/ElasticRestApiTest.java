package com.okguo.snailmall.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.okguo.snailmall.search.config.SnailmallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/25 13:41
 */
public class ElasticRestApiTest extends ElasticsearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setId(1);
        user.setName("zhangsan");
        user.setSex("男");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

        IndexResponse index = client.index(indexRequest, SnailmallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(JSON.toJSONString(index));

    }

    @Data
    static class User {
        private Integer id;
        private String name;
        private String sex;
    }

    @Test
    public void searchTest() throws IOException {
        //1.构建查询请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        //2.构建查询DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        //2.1)聚合1
        AggregationBuilder aggregationBuilder0 = AggregationBuilders.terms("ageAgg").field("age").size(10);

        searchSourceBuilder.aggregation(aggregationBuilder0);

        AvgAggregationBuilder aggregationBuilder1 = AggregationBuilders.avg("ageAvg").field("age");
        searchSourceBuilder.aggregation(aggregationBuilder1);

        AvgAggregationBuilder aggregationBuilder2 = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(aggregationBuilder2);


        System.out.println("查询请求==>" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);

        //3.执行DSL
        SearchResponse searchResponse = client.search(searchRequest, SnailmallElasticSearchConfig.COMMON_OPTIONS);

        //4.解析结果
        System.out.println("查询结果==>" + JSON.toJSONString(searchResponse));
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            Account account = JSONObject.parseObject(hit.getSourceAsString(), Account.class);
            System.out.println("数据==>" + JSON.toJSONString(account));
        }

        Aggregations aggregations = searchResponse.getAggregations();
//        for (Aggregation aggregation : aggregations.asList()) {
//            aggregation.getName();
//            aggregation.getMetaData();
//            aggregation.getType();
//        }
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            System.out.println("聚合解析数据==>年龄:" + bucket.getKeyAsString() + ",个数:" + bucket.getDocCount());
        }

        Avg ageAvg = aggregations.get("ageAvg");
        System.out.println("聚合解析数据==>平均年龄：" + ageAvg.getValue());

        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("聚合解析数据==>平均薪资：" + balanceAvg.getValue());
    }

    @Data
    static class Account {

        @JsonProperty("account_number")
        private int accountNumber;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
}