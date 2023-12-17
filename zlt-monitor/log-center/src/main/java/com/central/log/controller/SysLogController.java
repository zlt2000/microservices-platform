package com.central.log.controller;

import com.central.common.model.PageResult;
import com.central.log.model.TraceLog;
import com.central.log.service.TraceLogService;
import com.central.search.client.service.IQueryService;
import com.central.search.model.SearchDto;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 系统日志
 *
 * @author zlt
 */
@Tag(name = "系统日志")
@RestController
public class SysLogController {
    /**
     * 系统日志索引名
     */
    private static final String SYS_LOG_INDEXNAME = "sys-log-*";

    @Resource
    private IQueryService queryService;

    @Resource
    private TraceLogService traceLogService;

    @Operation(summary = "系统日志全文搜索列表")
    @GetMapping(value = "/sysLog")
    public PageResult<JsonNode> sysLog(SearchDto searchDto) {
        return queryService.strQuery(SYS_LOG_INDEXNAME, searchDto);
    }

    @Operation(summary = "系统日志链路列表")
    @GetMapping(value = "/traceLog")
    public PageResult<TraceLog> traceLog(SearchDto searchDto) {
        PageResult<JsonNode> pageResult = queryService.strQuery(SYS_LOG_INDEXNAME, searchDto);
        List<JsonNode> jsonNodeList = pageResult.getData();
        List<TraceLog> logList;
        if (jsonNodeList != null) {
            logList = traceLogService.transTraceLog(jsonNodeList);
        } else {
            logList = new ArrayList<>(0);
        }
        return PageResult.<TraceLog>builder().data(logList).code(0).count((long) logList.size()).build();
    }
}
