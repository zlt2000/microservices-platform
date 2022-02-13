package com.central.es.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.central.common.model.PageResult;
import com.central.common.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ES查询Builder
 *
 * @author zlt
 * @date 2020/3/28
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
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
    /**
     * 排序顺序
     */
    private static final String SORT_ORDER_ASC = "ASC";

    private SearchRequest searchRequest;
    private SearchSourceBuilder searchBuilder;
    private RestHighLevelClient client;

    private SearchBuilder(SearchRequest searchRequest, SearchSourceBuilder searchBuilder, RestHighLevelClient client) {
        this.searchRequest = searchRequest;
        this.searchBuilder = searchBuilder;
        this.client = client;
    }

    /**
     * 生成SearchBuilder实例
     * @param client
     * @param indexName
     */
    public static SearchBuilder builder(RestHighLevelClient client, String indexName) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        return new SearchBuilder(searchRequest, searchSourceBuilder, client);
    }

    /**
     * 生成SearchBuilder实例
     * @param client
     */
    public static SearchBuilder builder(RestHighLevelClient client) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        return new SearchBuilder(searchRequest, searchSourceBuilder, client);
    }

    /**
     * 设置索引名
     * @param indices 索引名数组
     */
    public SearchBuilder setIndices(String... indices) {
        if (ArrayUtil.isNotEmpty(indices)) {
            searchRequest.indices(indices);
        }
        return this;
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
        searchBuilder.query(queryBuilder);
        return this;
    }

    /**
     * 设置分页
     * @param page 当前页数
     * @param limit 每页显示数
     */
    public SearchBuilder setPage(Integer page, Integer limit) {
        setPage(page, limit, false);
        return this;
    }

    /**
     * 设置分页
     * @param page 当前页数
     * @param limit 每页显示数
     * @param trackTotalHits 分页总数是否显示所有条数，默认只显示10000
     */
    public SearchBuilder setPage(Integer page, Integer limit, boolean trackTotalHits) {
        if (page != null && limit != null) {
            searchBuilder.from((page - 1) * limit)
                    .size(limit);
            if (trackTotalHits) {
                searchBuilder.trackTotalHits(trackTotalHits);
            }

        }
        return this;
    }

    /**
     * 增加排序
     * @param field 排序字段
     * @param order 顺序方向
     */
    public SearchBuilder addSort(String field, String order) {
        if (StrUtil.isNotEmpty(field) && order != null) {
            SortOrder so;
            if (SORT_ORDER_ASC.equals(order)) {
                so = SortOrder.ASC;
            } else {
                so = SortOrder.DESC;
            }
            searchBuilder.sort(field, so);
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
            searchRequest.routing(routing);
        }
        return this;
    }

    /**
     * 返回结果 SearchResponse
     */
    public SearchResponse get() throws IOException {
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * 返回列表结果 List<JsonNode>
     */
    public List<JsonNode> getList() throws IOException {
        return getList(this.get().getHits());
    }

    /**
     * 返回分页结果 PageResult<JSONObject>
     */
    public PageResult<JsonNode> getPage() throws IOException {
        return this.getPage(null, null);
    }

    /**
     * 返回分页结果 PageResult<JSONObject>
     * @param page 当前页数
     * @param limit 每页显示
     */
    public PageResult<JsonNode> getPage(Integer page, Integer limit) throws IOException {
        this.setPage(page, limit);
        SearchResponse response = this.get();
        SearchHits searchHits = response.getHits();
        long totalCnt = searchHits.getTotalHits().value;
        List<JsonNode> list = getList(searchHits);
        return PageResult.<JsonNode>builder().data(list).code(0).count(totalCnt).build();
    }

    /**
     * 返回JSON列表数据
     */
    private List<JsonNode> getList(SearchHits searchHits) {
        List<JsonNode> list = new ArrayList<>();
        if (searchHits != null) {
            searchHits.forEach(item -> {
                JsonNode jsonNode = JsonUtil.parse(item.getSourceAsString());
                ObjectNode objectNode = (ObjectNode)jsonNode;
                objectNode.put("id", item.getId());

                Map<String, HighlightField> highlightFields = item.getHighlightFields();
                if (highlightFields != null) {
                    populateHighLightedFields(objectNode, highlightFields);
                }
                list.add(objectNode);
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
                    if (result instanceof ObjectNode) {
                        ((ObjectNode)result).put(field.getName(), concat(field.fragments()));
                    } else {
                        PropertyUtils.setProperty(result, field.getName(), concat(field.fragments()));
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new UncategorizedElasticsearchException("failed to set highlighted value for field: " + field.getName()
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
