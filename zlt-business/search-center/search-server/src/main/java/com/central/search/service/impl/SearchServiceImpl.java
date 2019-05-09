package com.central.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PageResult;
import com.central.search.model.SearchDto;
import com.central.search.service.ISearchService;
import com.central.search.util.SearchBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

/**
 * 通用搜索
 *
 * @author zlt
 * @date 2019/4/24
 */
@Service
public class SearchServiceImpl implements ISearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * StringQuery通用搜索
     * @param indexName 索引名
     * @param searchDto 搜索Dto
     * @return
     */
    @Override
    public PageResult<JSONObject> strQuery(String indexName, SearchDto searchDto) {
        return SearchBuilder.builder(elasticsearchTemplate, indexName)
                .setStringQuery(searchDto.getQueryStr())
                .addSort(searchDto.getSortCol(), SortOrder.DESC)
                .setIsHighlight(searchDto.getIsHighlighter())
                .getPage(searchDto.getPage(), searchDto.getLimit());
    }
}
