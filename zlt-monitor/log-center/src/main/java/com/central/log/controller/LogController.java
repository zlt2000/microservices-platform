package com.central.log.controller;

import com.central.common.model.PageResult;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 日志父类
 *
 * @author zlt
 */
public class LogController<E> {
    public PageResult<E> getPage(@RequestParam Map<String, Object> params, BoolQueryBuilder builder, ElasticsearchRepository repository) {
        // 分页参数
        Pageable pageable = PageRequest.of(MapUtils.getInteger(params, "page") - 1, MapUtils.getInteger(params, "limit")
                , Sort.Direction.DESC, "timestamp");
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(builder).withPageable(pageable).build();
        Page<E> result = (Page<E>)repository.search(query);
        return PageResult.<E>builder().data(result.getContent()).code(0).count(result.getTotalElements()).build();
    }
}
