package com.central.search.controller;

import com.central.common.model.*;

import com.central.search.model.SearchDto;
import com.central.search.service.ISearchService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 通用搜索
 *
 * @author zlt
 */
@Slf4j
@RestController
@Tag(name = "搜索模块api")
@RequestMapping("/search")
public class SearchController {
    private final ISearchService searchService;

    public SearchController(ISearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 查询文档列表
     * @param indexName 索引名
     * @param searchDto 搜索Dto
     */
    @PostMapping("/{indexName}")
    public PageResult<JsonNode> strQuery(@PathVariable String indexName, @RequestBody(required = false) SearchDto searchDto) throws IOException {
        if (searchDto == null) {
            searchDto = new SearchDto();
        }
        return searchService.strQuery(indexName, searchDto);
    }
}