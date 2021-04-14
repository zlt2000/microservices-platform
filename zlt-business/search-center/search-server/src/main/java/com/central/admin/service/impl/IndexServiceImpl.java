package com.central.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.central.admin.model.IndexDto;
import com.central.admin.service.IIndexService;
import com.central.common.model.PageResult;
import com.central.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 索引
 *
 * @author zlt
 * @date 2019/4/23
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Service
public class IndexServiceImpl implements IIndexService {
    private ObjectMapper mapper = new ObjectMapper();

    private final RestHighLevelClient client;

    public IndexServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public boolean create(IndexDto indexDto) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexDto.getIndexName());
        request.settings(Settings.builder()
                .put("index.number_of_shards", indexDto.getNumberOfShards())
                .put("index.number_of_replicas", indexDto.getNumberOfReplicas())
        );
        if (StrUtil.isNotEmpty(indexDto.getMappingsSource())) {
            //mappings
            request.mapping(indexDto.getMappingsSource(), XContentType.JSON);
        }
        CreateIndexResponse response = client
                .indices()
                .create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    @Override
    public boolean delete(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    @Override
    public PageResult<Map<String, String>> list(String queryStr, String indices) throws IOException {
        if (StrUtil.isNotEmpty(queryStr)) {
            indices = queryStr;
        }
        Response response = client.getLowLevelClient()
                .performRequest(new Request(
                        "GET",
                        "/_cat/indices?h=health,status,index,docsCount,docsDeleted,storeSize&s=cds:desc&format=json&index="+StrUtil.nullToEmpty(indices)
                ));

        List<Map<String, String>> listOfIndicesFromEs = null;
        if (response != null) {
            String rawBody = EntityUtils.toString(response.getEntity());
            TypeReference<List<Map<String, String>>> typeRef = new TypeReference<List<Map<String, String>>>() {};
            listOfIndicesFromEs = mapper.readValue(rawBody, typeRef);
        }
        return PageResult.<Map<String, String>>builder().data(listOfIndicesFromEs).code(0).build();
    }

    @Override
    public Map<String, Object> show(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        GetIndexResponse getIndexResponse = client
                .indices().get(request, RequestOptions.DEFAULT);
        MappingMetadata mappingMetadata = getIndexResponse.getMappings().get(indexName);
        Map<String, Object> mappOpenMap = mappingMetadata.getSourceAsMap();
        List<AliasMetadata> indexAliases = getIndexResponse.getAliases().get(indexName);

        String settingsStr = getIndexResponse.getSettings().get(indexName).toString();
        Object settingsObj = null;
        if (StrUtil.isNotEmpty(settingsStr)) {
            settingsObj = JsonUtil.parse(settingsStr);
        }
        Map<String, Object> result = new HashMap<>(1);
        Map<String, Object> indexMap = new HashMap<>(3);
        List<String> aliasesList = new ArrayList<>(indexAliases.size());
        indexMap.put("aliases", aliasesList);
        indexMap.put("settings", settingsObj);
        indexMap.put("mappings", mappOpenMap);
        result.put(indexName, indexMap);
        //获取aliases数据
        for (AliasMetadata aliases : indexAliases) {
            aliasesList.add(aliases.getAlias());
        }
        return result;
    }
}
