package com.central.search.service;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PageResult;
import com.central.search.model.SearchDto;

/**
 * @author zlt
 * @date 2019/4/24
 */
public interface ISearchService {
    /**
     * StringQuery通用搜索
     * @param indexName 索引名
     * @param searchDto 搜索Dto
     * @return
     */
    PageResult<JSONObject> strQuery(String indexName, SearchDto searchDto);
}
