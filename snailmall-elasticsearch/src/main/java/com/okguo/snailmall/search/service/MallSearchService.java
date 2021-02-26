package com.okguo.snailmall.search.service;

import com.okguo.snailmall.search.vo.SearchParam;
import com.okguo.snailmall.search.vo.SearchResult;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/02/25 11:07
 */
public interface MallSearchService {
    SearchResult search(SearchParam param);
}
