package com.central.log.controller;

import com.central.common.model.PageResult;
import com.central.search.client.service.IQueryService;
import com.central.search.model.SearchDto;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 慢查询日志
 *
 * @author zlt
 */
@Tag(name = "慢查询日志")
@RestController
public class SlowQueryLogController {
    @Autowired
    private IQueryService queryService;

    @Operation(summary = "慢sql日志全文搜索列表")
    @GetMapping(value = "/slowQueryLog")
    public PageResult<JsonNode> getPage(SearchDto searchDto) {
        searchDto.setIsHighlighter(true);
        searchDto.setSortCol("timestamp");
        return queryService.strQuery("mysql-slowlog-*", searchDto);
    }
}
