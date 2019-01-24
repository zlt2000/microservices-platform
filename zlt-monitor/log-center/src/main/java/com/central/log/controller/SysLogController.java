package com.central.log.controller;

import java.util.*;

import cn.hutool.core.util.StrUtil;
import com.central.common.model.PageResult;
import com.central.log.dao.SysLogDao;
import com.central.log.model.SysLog;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志
 *
 * @author zlt
 */
@RestController
public class SysLogController extends LogController<SysLog> {
    private static final String ES_PARAM_MESSAGE = "message";
    private static final String ES_PARAM_LOG_LEVEL = "logLevel";
    private static final String ES_PARAM_APP_NAME = "appName";
    private static final String ES_PARAM_CLASSNAME = "classname";

    @Autowired
    public SysLogDao logDao;

    @ApiOperation(value = "系统日志查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/sysLog")
    public PageResult<SysLog> getPage(@RequestParam Map<String, Object> params) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        String searchKey = (String) params.get("searchKey");
        String searchValue = (String) params.get("searchValue");
        if (StrUtil.isNotEmpty(searchKey) && StrUtil.isNotEmpty(searchValue)) {
            if (ES_PARAM_LOG_LEVEL.equals(searchKey)) {
                searchValue = searchValue.toUpperCase();
            }
            // 模糊查询
            builder.must(QueryBuilders.matchQuery(searchKey, searchValue));
        }
        return super.getPage(params, builder, logDao);
    }
}
