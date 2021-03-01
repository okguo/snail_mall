package com.okguo.snailmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.okguo.common.to.es.SkuEsModel;
import com.okguo.common.utils.R;
import com.okguo.snailmall.search.config.SnailmallElasticSearchConfig;
import com.okguo.snailmall.search.constant.EsConstant;
import com.okguo.snailmall.search.feign.ProductFeignService;
import com.okguo.snailmall.search.service.MallSearchService;
import com.okguo.snailmall.search.vo.AttrResponseVO;
import com.okguo.snailmall.search.vo.SearchParam;
import com.okguo.snailmall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/02/25 11:07
 */
@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {
        SearchRequest searchRequest = this.buildSearchRequest(param);
        SearchResult result = null;
        try {
            SearchResponse searchResponse = client.search(searchRequest, SnailmallElasticSearchConfig.COMMON_OPTIONS);
            result = this.buildSearchResult(param, searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 构建查询dsl，#模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮，聚合分析
     *
     * @param param
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /*
         * 过滤（按照属性，分类，品牌，价格区间，库存）
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attrStr : param.getAttrs()) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                boolQuery.filter(QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None));
            }
        }

        if (StringUtils.isNotEmpty(param.getSkuPrice())) {
            //skuPrice=1_500/_500/500_
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            if (param.getSkuPrice().startsWith("_")) {
                rangeQuery.lte(param.getSkuPrice().replace("_", ""));
            } else if (param.getSkuPrice().endsWith("_")) {
                rangeQuery.gte(param.getSkuPrice().replace("_", ""));
            } else {
                String[] s = param.getSkuPrice().split("_");
                rangeQuery.gte(s[0]);
                rangeQuery.lte(s[1]);
            }
            boolQuery.filter(rangeQuery);
        }
        sourceBuilder.query(boolQuery);
        /*
         * 排序，分页，高亮
         */
        if (StringUtils.isNotEmpty(param.getSort())) {
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], sortOrder);
        }

        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        if (StringUtils.isNotEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        /*
         * 聚合分析
         */
        //品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        sourceBuilder.aggregation(brand_agg);
        //分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        sourceBuilder.aggregation(catalog_agg);
        //属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

        log.info("sourceBuilder->DDL" + sourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    private SearchResult buildSearchResult(SearchParam param, SearchResponse searchResponse) {
        SearchResult result = new SearchResult();
        SearchHits hits = searchResponse.getHits();
        //解析商品列表
        List<SkuEsModel> collect = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            collect = Arrays.stream(hits.getHits()).map(e -> {
                SkuEsModel esModel = JSON.parseObject(e.getSourceAsString(), SkuEsModel.class);
                if (StringUtils.isNotEmpty(param.getKeyword())) {
                    // 设置高亮
                    esModel.setSkuTitle(e.getHighlightFields().get("skuTitle").getFragments()[0].string());
                }
                return esModel;
            }).collect(Collectors.toList());
        }
        result.setProducts(collect);
        //聚合 解析品牌信息
        List<SearchResult.BrandVo> brandVoList = new ArrayList<>();
        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        if (brand_agg.getBuckets() != null && brand_agg.getBuckets().size() > 0) {
            brandVoList = brand_agg.getBuckets().stream().map(e -> {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                brandVo.setBrandId(e.getKeyAsNumber().longValue());
                brandVo.setBrandName(((ParsedStringTerms) e.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString());
                brandVo.setBrandImg(((ParsedStringTerms) e.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString());
                return brandVo;
            }).collect(Collectors.toList());
        }
        result.setBrands(brandVoList);

        //聚合 解析屬性信息
        List<SearchResult.AttrVo> attrVoList = new ArrayList<>();
        ParsedNested attr_agg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        if (attr_id_agg.getBuckets() != null && attr_id_agg.getBuckets().size() > 0) {
            attrVoList = attr_id_agg.getBuckets().stream().map(e -> {
                SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                attrVo.setAttrId(e.getKeyAsNumber().longValue());
                attrVo.setAttrName(((ParsedStringTerms) e.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString());
                ParsedStringTerms attr_value_agg = e.getAggregations().get("attr_value_agg");
                if (attr_value_agg.getBuckets() != null && attr_value_agg.getBuckets().size() > 0) {
                    attrVo.setAttrValue(attr_value_agg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList()));
                } else {
                    attrVo.setAttrValue(new ArrayList<>());
                }
                return attrVo;
            }).collect(Collectors.toList());
        }
        result.setAttrs(attrVoList);

        //聚合 解析分类信息
        ParsedLongTerms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
        if (catalog_agg.getBuckets() != null && catalog_agg.getBuckets().size() > 0) {
            catalogVoList = catalog_agg.getBuckets().stream().map(e -> {
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
                catalogVo.setCatalogId(Long.parseLong(e.getKeyAsString()));
                ParsedStringTerms catalog_name_agg = e.getAggregations().get("catalog_name_agg");
                catalogVo.setCatalogName(catalog_name_agg.getBuckets().get(0).getKeyAsString());
                return catalogVo;
            }).collect(Collectors.toList());
        }
        result.setCatalogs(catalogVoList);
//
        result.setPageNum(param.getPageNum());
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        int totalPages = (int) total % EsConstant.PRODUCT_PAGE_SIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGE_SIZE : (int) total / EsConstant.PRODUCT_PAGE_SIZE + 1;
        result.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVO attr1 = r.getData("attr", new TypeReference<AttrResponseVO>() {
                    });
                    navVo.setNavName(attr1.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr, "utf-8");
                    encode = encode.replace("+", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String replace = param.get_queryString().replace("&attr" + encode, "");
                navVo.setLink("http://search.snailmall.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }else {
            result.setNavs(new ArrayList<>());
        }

        log.info("buildSearchResult->result:" + JSON.toJSONString(result));
        return result;
    }
}
