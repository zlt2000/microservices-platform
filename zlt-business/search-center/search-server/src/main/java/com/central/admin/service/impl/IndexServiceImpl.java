package com.central.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.central.admin.model.IndexDto;
import com.central.admin.service.IIndexService;
import com.central.common.model.PageResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 索引
 *
 * @author zlt
 * @date 2019/4/23
 */
@Service
public class IndexServiceImpl implements IIndexService {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean create(IndexDto indexDto) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexDto.getIndexName());
        request.settings(Settings.builder()
                .put("index.number_of_shards", indexDto.getNumberOfShards())
                .put("index.number_of_replicas", indexDto.getNumberOfReplicas())
        );
        if (StrUtil.isNotEmpty(indexDto.getType()) && StrUtil.isNotEmpty(indexDto.getMappingsSource())) {
            //mappings
            request.mapping(indexDto.getType(), indexDto.getMappingsSource(), XContentType.JSON);
        }
        CreateIndexResponse response = elasticsearchRestTemplate.getClient()
                .indices()
                .create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    @Override
    public boolean delete(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse response = elasticsearchRestTemplate.getClient().indices().delete(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    @Override
    public PageResult<Map<String, String>> list(String queryStr, String indices) throws IOException {
        Response response = elasticsearchRestTemplate.getClient().getLowLevelClient()
                .performRequest(new Request(
                        "GET",
                        "/_cat/indices?h=health,status,index,docsCount,docsDeleted,storeSize&s=cds:desc&format=json&index="+StrUtil.nullToEmpty(indices)
                ));

        List<Map<String, String>> listOfIndicesFromEs = null;
        if (response != null) {
            String rawBody = EntityUtils.toString(response.getEntity());
            TypeReference<List<HashMap<String, String>>> typeRef = new TypeReference<List<HashMap<String, String>>>() {};
            listOfIndicesFromEs = mapper.readValue(rawBody, typeRef);
        }
        return PageResult.<Map<String, String>>builder().data(listOfIndicesFromEs).code(0).build();
    }

    /**
     * bytes 转换为 kb
     */
    private Double getKB(Long bytes) {
        if (bytes == null) {
            return 0D;
        }
        return bytes / 1024D;
    }

    @Override
    public Map<String, Object> show(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(indexName);
        GetIndexResponse getIndexResponse = elasticsearchRestTemplate.getClient()
                .indices().get(request, RequestOptions.DEFAULT);
        ImmutableOpenMap<String, MappingMetaData> mappOpenMap = getIndexResponse.getMappings().get(indexName);
        List<AliasMetaData> indexAliases = getIndexResponse.getAliases().get(indexName);

        String settingsStr = getIndexResponse.getSettings().get(indexName).toString();
        Object settingsObj = null;
        if (StrUtil.isNotEmpty(settingsStr)) {
            settingsObj = JSONObject.parse(settingsStr);
        }
        Map<String, Object> result = new HashMap<>(1);
        Map<String, Object> indexMap = new HashMap<>(3);
        Map<String, Object> mappMap = new HashMap<>(mappOpenMap.size());
        List<String> aliasesList = new ArrayList<>(indexAliases.size());
        indexMap.put("aliases", aliasesList);
        indexMap.put("settings", settingsObj);
        indexMap.put("mappings", mappMap);
        result.put(indexName, indexMap);
        //获取mappings数据
        for (ObjectCursor<String> key : mappOpenMap.keys()) {
            MappingMetaData data = mappOpenMap.get(key.value);
            Map<String, Object> dataMap = data.getSourceAsMap();
            mappMap.put(key.value, dataMap);
        }
        //获取aliases数据
        for (AliasMetaData aliases : indexAliases) {
            aliasesList.add(aliases.getAlias());
        }
        return result;
    }
}
