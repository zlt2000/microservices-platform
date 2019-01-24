package com.central.log.controller;

import cn.hutool.core.util.StrUtil;
import com.central.common.model.PageResult;
import com.central.log.dao.SlowQueryLogDao;
import com.central.log.model.SlowQueryLog;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 慢查询日志
 *
 * @author zlt
 */
@RestController
public class SlowQueryLogController extends LogController<SlowQueryLog> {
    private static final String ES_PARAM_QUERY = "query_str";

    @Autowired
    public SlowQueryLogDao logDao;

    @ApiOperation(value = "系统日志查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/slowQueryLog")
    public PageResult<SlowQueryLog> getPage(@RequestParam Map<String, Object> params) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        String searchKey = (String) params.get("searchKey");
        String searchValue = (String) params.get("searchValue");
        if (StrUtil.isNotEmpty(searchKey) && StrUtil.isNotEmpty(searchValue)) {
            // 模糊查询
            builder.must(QueryBuilders.matchPhraseQuery(ES_PARAM_QUERY, searchValue));
        }

        return super.getPage(params, builder, logDao);
    }
}
