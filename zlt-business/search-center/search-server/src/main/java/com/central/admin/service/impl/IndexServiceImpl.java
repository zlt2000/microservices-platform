package com.central.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.central.admin.model.IndexDto;
import com.central.admin.model.IndexVo;
import com.central.admin.service.IIndexService;
import com.central.common.model.PageResult;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequestBuilder;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.search.stats.SearchStats;
import org.elasticsearch.index.shard.DocsStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

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
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void create(IndexDto indexDto) {
        // setting
        Settings settings = Settings.builder()
                .put("index.number_of_shards", indexDto.getNumberOfShards())
                .put("index.number_of_replicas", indexDto.getNumberOfReplicas())
                .build();
        CreateIndexRequestBuilder builder = elasticsearchTemplate.getClient().admin()
                .indices()
                .prepareCreate(indexDto.getIndexName())
                .setSettings(settings);
        if (StrUtil.isNotEmpty(indexDto.getType()) && StrUtil.isNotEmpty(indexDto.getMappingsSource())) {
            //mappings
            builder.addMapping(indexDto.getType(), indexDto.getMappingsSource(), XContentType.JSON);
        }
        builder.get();
    }

    @Override
    public void delete(String indexName) {
        elasticsearchTemplate.getClient().admin().indices()
                .prepareDelete(indexName)
                .execute().actionGet();
    }

    @Override
    public PageResult<IndexVo> list(String queryStr, String... indices) {
        IndicesStatsRequestBuilder indicesBuilder = elasticsearchTemplate.getClient().admin().indices()
                .prepareStats(indices);
        if (StrUtil.isNotEmpty(queryStr)) {
            indicesBuilder.setIndices(queryStr);
        }
        List<IndexVo> indexList = new ArrayList<>();
        try {
            IndicesStatsResponse response = indicesBuilder.execute().actionGet();
            Map<String, IndexStats> indicesMap = response.getIndices();
            for(IndexStats stat : indicesMap.values()) {
                IndexVo vo = new IndexVo();
                vo.setIndexName(stat.getIndex());
                //获取文档数据
                DocsStats docsStats = stat.getTotal().getDocs();
                vo.setDocsCount(docsStats.getCount());
                vo.setDocsDeleted(docsStats.getDeleted());
                //获取存储数据
                vo.setStoreSizeInBytes(getKB(stat.getTotal().getStore().getSizeInBytes()));
                //获取查询数据
                SearchStats.Stats searchStats = stat.getTotal().getSearch().getTotal();
                vo.setQueryCount(searchStats.getQueryCount());
                vo.setQueryTimeInMillis(searchStats.getQueryTimeInMillis() / 1000D);

                indexList.add(vo);
            }
        } catch (IndexNotFoundException e) {}
        return PageResult.<IndexVo>builder().data(indexList).code(0).build();
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
    public Map<String, Object> show(String indexName) {
        ImmutableOpenMap<String, IndexMetaData>  stat = elasticsearchTemplate.getClient().admin().cluster()
                .prepareState().setIndices(indexName).execute().actionGet()
                .getState()
                .getMetaData()
                .getIndices();

        IndexMetaData indexMetaData = stat.get(indexName);
        //获取settings数据
        String settingsStr = indexMetaData.getSettings().toString();
        Object settingsObj = null;
        if (StrUtil.isNotEmpty(settingsStr)) {
            settingsObj = JSONObject.parse(settingsStr);
        }

        ImmutableOpenMap<String, MappingMetaData> mappOpenMap = indexMetaData.getMappings();
        ImmutableOpenMap<String, AliasMetaData> aliasesOpenMap = indexMetaData.getAliases();
        Map<String, Object> result = new HashMap<>(1);
        Map<String, Object> indexMap = new HashMap<>(3);
        Map<String, Object> mappMap = new HashMap<>(mappOpenMap.size());
        Map<String, Object> aliasesMap = new HashMap<>(aliasesOpenMap.size());
        indexMap.put("aliases", aliasesMap);
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
        for (ObjectCursor<String> key : aliasesOpenMap.keys()) {
            AliasMetaData data = aliasesOpenMap.get(key.value);
            aliasesMap.put(key.value, data.alias());
        }
        return result;
    }
}
