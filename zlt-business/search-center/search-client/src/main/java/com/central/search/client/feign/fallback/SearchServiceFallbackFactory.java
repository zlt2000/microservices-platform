package com.central.search.client.feign.fallback;

import com.central.common.model.PageResult;
import com.central.search.client.feign.SearchService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * searchService降级工场
 *
 * @author zlt
 */
@Slf4j
@Component
public class SearchServiceFallbackFactory implements FallbackFactory<SearchService> {
    @Override
    public SearchService create(Throwable throwable) {
        return (indexName, searchDto) -> {
            log.error("通过索引{}搜索异常:{}", indexName, throwable);
            return PageResult.<JsonNode>builder().build();
        };
    }
}
