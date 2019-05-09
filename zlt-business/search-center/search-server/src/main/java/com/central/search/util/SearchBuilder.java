package com.central.search.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PageResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ES查询Builder
 *
 * @author zlt
 * @date 2019/4/23
 */
@Setter
@Getter
public class SearchBuilder {
    /**
     * 高亮前缀
     */
    private static final String HIGHLIGHTER_PRE_TAGS = "<mark>";
    /**
     * 高亮后缀
     */
    private static final String HIGHLIGHTER_POST_TAGS = "</mark>";

    private SearchRequestBuilder searchBuilder;

    private SearchBuilder(SearchRequestBuilder searchBuilder) {
        this.searchBuilder = searchBuilder;
    }

    /**
     * 生成SearchBuilder实例
     * @param elasticsearchTemplate
     * @param indexName
     */
    public static SearchBuilder builder(ElasticsearchTemplate elasticsearchTemplate, String indexName) {
        SearchRequestBuilder searchBuilder = elasticsearchTemplate.getClient().prepareSearch(indexName);
        return new SearchBuilder(searchBuilder);
    }

    /**
     * 生成queryStringQuery查询
     * @param queryStr 查询关键字
     */
    public SearchBuilder setStringQuery(String queryStr) {
        QueryBuilder queryBuilder;
        if (StrUtil.isNotEmpty(queryStr)) {
            queryBuilder = QueryBuilders.queryStringQuery(queryStr);
        } else {
            queryBuilder = QueryBuilders.matchAllQuery();
        }
        searchBuilder.setQuery(queryBuilder);
        return this;
    }

    /**
     * 设置分页
     * @param page 当前页数
     * @param limit 每页显示数
     */
    public SearchBuilder setPage(Integer page, Integer limit) {
        if (page != null && limit != null) {
            searchBuilder.setFrom((page - 1) * limit)
                    .setSize(limit);
        }
        return this;
    }

    /**
     * 增加排序
     * @param field 排序字段
     * @param order 顺序方向
     */
    public SearchBuilder addSort(String field, SortOrder order) {
        if (StrUtil.isNotEmpty(field) && order != null) {
            searchBuilder.addSort(field, order);
        }
        return this;
    }

    /**
     * 设置高亮
     * @param preTags 高亮处理前缀
     * @param postTags 高亮处理后缀
     */
    public SearchBuilder setHighlight(String field, String preTags, String postTags) {
        if (StrUtil.isNotEmpty(field) && StrUtil.isNotEmpty(preTags) && StrUtil.isNotEmpty(postTags)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field(field)
                    .preTags(preTags)
                    .postTags(postTags);
            searchBuilder.highlighter(highlightBuilder);
        }
        return this;
    }

    /**
     * 设置是否需要高亮处理
     * @param isHighlighter 是否需要高亮处理
     */
    public SearchBuilder setIsHighlight(Boolean isHighlighter) {
        if (BooleanUtil.isTrue(isHighlighter)) {
            this.setHighlight("*"
                    , HIGHLIGHTER_PRE_TAGS, HIGHLIGHTER_POST_TAGS);
        }
        return this;
    }

    /**
     * 设置查询路由
     * @param routing 路由数组
     */
    public SearchBuilder setRouting(String... routing) {
        if (ArrayUtil.isNotEmpty(routing)) {
            searchBuilder.setRouting(routing);
        }
        return this;
    }

    /**
     * 返回结果 SearchResponse
     */
    public SearchResponse get() {
        return searchBuilder.execute().actionGet();
    }

    /**
     * 返回列表结果 List<JSONObject>
     */
    public List<JSONObject> getList() {
        return getList(this.get().getHits());
    }

    /**
     * 返回分页结果 PageResult<JSONObject>
     */
    public PageResult<JSONObject> getPage() {
        return this.getPage(null, null);
    }

    /**
     * 返回分页结果 PageResult<JSONObject>
     * @param page 当前页数
     * @param limit 每页显示
     */
    public PageResult<JSONObject> getPage(Integer page, Integer limit) {
        this.setPage(page, limit);
        SearchResponse response = this.get();
        SearchHits searchHits = response.getHits();
        long totalCnt = searchHits.getTotalHits();
        List<JSONObject> list = getList(searchHits);
        return PageResult.<JSONObject>builder().data(list).code(0).count(totalCnt).build();
    }

    /**
     * 返回JSON列表数据
     */
    private List<JSONObject> getList(SearchHits searchHits) {
        List<JSONObject> list = new ArrayList<>();
        if (searchHits != null) {
            searchHits.forEach(item -> {
                JSONObject jsonObject = JSON.parseObject(item.getSourceAsString());
                jsonObject.put("id", item.getId());

                Map<String, HighlightField> highlightFields = item.getHighlightFields();
                if (highlightFields != null) {
                    populateHighLightedFields(jsonObject, highlightFields);
                }
                list.add(jsonObject);
            });
        }
        return list;
    }

    /**
     * 组装高亮字符
     * @param result 目标对象
     * @param highlightFields 高亮配置
     */
    private <T> void populateHighLightedFields(T result, Map<String, HighlightField> highlightFields) {
        for (HighlightField field : highlightFields.values()) {
            try {
                String name = field.getName();
                if (!name.endsWith(".keyword")) {
                    PropertyUtils.setProperty(result, field.getName(), concat(field.fragments()));
                }
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new ElasticsearchException("failed to set highlighted value for field: " + field.getName()
                        + " with value: " + Arrays.toString(field.getFragments()), e);
            }
        }
    }
    /**
     * 拼凑数组为字符串
     */
    private String concat(Text[] texts) {
        StringBuffer sb = new StringBuffer();
        for (Text text : texts) {
            sb.append(text.toString());
        }
        return sb.toString();
    }
}
