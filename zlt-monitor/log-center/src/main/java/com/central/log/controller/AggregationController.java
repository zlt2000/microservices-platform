package com.central.log.controller;

import com.central.search.client.service.IQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 访问统计
 *
 * @author zlt
 * @date 2019/5/8
 */
@Tag(name = "访问统计")
@RestController
public class AggregationController {
    @Autowired
    private IQueryService queryService;

    @Operation(summary = "访问统计")
    @GetMapping(value = "/requestStat")
    public Map<String, Object> requestStatAgg() {
        return queryService.requestStatAgg("point-log-*","request-statistics");
    }
}
