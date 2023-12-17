package com.central.common.lb;

import com.central.common.lb.config.RestTemplateProperties;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author zlt
 * @date 2018/11/17
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
@EnableConfigurationProperties(RestTemplateProperties.class)
public class RestTemplateAutoConfigure {
    @Autowired
    private RestTemplateProperties restTemplateProperties;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }

    /**
     * httpclient 实现的ClientHttpRequestFactory
     */
    @Bean
    public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    /**
     * 使用连接池的 httpclient
     */
    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        // 最大链接数
        connectionManager.setMaxTotal(restTemplateProperties.getMaxTotal());
        // 同路由并发数20
        connectionManager.setDefaultMaxPerRoute(restTemplateProperties.getMaxPerRoute());

        ConnectionConfig connectConfig = ConnectionConfig.custom()
                // 读超时
                .setSocketTimeout(Timeout.ofMicroseconds(restTemplateProperties.getReadTimeout()))
                // 链接超时
                .setConnectTimeout(Timeout.ofMicroseconds(restTemplateProperties.getConnectTimeout()))
                .build();
        connectionManager.setDefaultConnectionConfig(connectConfig);

        RequestConfig requestConfig = RequestConfig.custom()
                // 链接不够用的等待时间
                .setConnectionRequestTimeout(Timeout.ofMicroseconds(restTemplateProperties.getReadTimeout()))
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }
}
