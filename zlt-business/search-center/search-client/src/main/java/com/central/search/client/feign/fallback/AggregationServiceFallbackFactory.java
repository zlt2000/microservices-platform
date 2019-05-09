package com.central.search.client.feign.fallback;

import cn.hutool.core.map.MapUtil;
import com.central.search.client.feign.AggregationService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * searchService降级工场
 *
 * @author zlt
 */
@Slf4j
@Component
public class AggregationServiceFallbackFactory implements FallbackFactory<AggregationService> {
    @Override
    public AggregationService create(Throwable throwable) {
        return (indexName, routing) -> {
            log.error("通过索引{}搜索异常:{}", indexName, throwable);
            return MapUtil.newHashMap(0);
        };
    }
}
