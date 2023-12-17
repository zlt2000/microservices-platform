package com.central.es.config;

import com.central.es.properties.RestClientPoolProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

import javax.annotation.Resource;
import java.util.List;

/**
 * es自动配置
 *
 * @author zlt
 * @date 2020/3/28
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@EnableConfigurationProperties(RestClientPoolProperties.class)
public class RestAutoConfigure extends AbstractElasticsearchConfiguration {
    private final static String SCHEME = "http";
    private final static String URI_SPLIT = ":";

    @Resource
    private ElasticsearchProperties restProperties;

    @Resource
    private RestClientPoolProperties poolProperties;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        List<String> urlArr = restProperties.getUris();
        HttpHost[] httpPostArr = new HttpHost[urlArr.size()];
        for (int i = 0; i < urlArr.size(); i++) {
            HttpHost httpHost = new HttpHost(urlArr.get(i).split(URI_SPLIT)[0].trim(),
                    Integer.parseInt(urlArr.get(i).split(URI_SPLIT)[1].trim()), SCHEME);
            httpPostArr[i] = httpHost;
        }
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(restProperties.getUsername(), restProperties.getPassword()));
        RestClientBuilder builder = RestClient.builder(httpPostArr)
                // 异步httpclient连接数配置
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    // 账号密码登录
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    // httpclient连接数配置
                    httpClientBuilder.setMaxConnTotal(poolProperties.getMaxConnectNum())
                                     .setMaxConnPerRoute(poolProperties.getMaxConnectPerRoute());
                    return httpClientBuilder;
                })
                // 异步httpclient连接延时配置
                .setRequestConfigCallback(requestConfigBuilder -> {
                    requestConfigBuilder.setConnectTimeout(poolProperties.getConnectTimeOut())
                            .setSocketTimeout(poolProperties.getSocketTimeOut())
                            .setConnectionRequestTimeout(poolProperties.getConnectionRequestTimeOut());
                    return requestConfigBuilder;
                });
        return new RestHighLevelClient(builder);
    }
}
