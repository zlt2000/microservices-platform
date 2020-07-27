package com.central.oauth.service.impl;

import com.central.common.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author zlt
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 类说明
 * 将oauth_client_details表数据缓存到redis，这里做个缓存优化
 * layui模块中有对oauth_client_details的crud， 注意同步redis的数据
 * 注意对oauth_client_details清楚redis db部分数据的清空
 */
@Slf4j
@Service
public class RedisClientDetailsService extends JdbcClientDetailsService {
    private RedisTemplate<String, Object> redisTemplate;

    public RedisClientDetailsService(DataSource dataSource, RedisTemplate<String, Object> redisTemplate) {
        super(dataSource);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        // 先从redis获取
        ClientDetails clientDetails = (ClientDetails) redisTemplate.opsForValue().get(clientRedisKey(clientId));
        if (clientDetails == null) {
            clientDetails = cacheAndGetClient(clientId);
        }
        return clientDetails;
    }

    /**
     * 缓存client并返回client
     * @param clientId
     * @return
     */
    private ClientDetails cacheAndGetClient(String clientId) {
        // 从数据库读取
        ClientDetails clientDetails = null;
        try {
            clientDetails = super.loadClientByClientId(clientId);
            if (clientDetails != null) {
                // 写入redis缓存
                redisTemplate.opsForValue().set(clientRedisKey(clientId), clientDetails);
                log.info("缓存clientId:{},{}", clientId, clientDetails);
            }
        } catch (NoSuchClientException e) {
            log.error("clientId:{},{}", clientId, clientId);
        } catch (InvalidClientException e) {
            log.error("cacheAndGetClient-invalidClient:{}", clientId, e);
        }
        return clientDetails;
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) {
        super.updateClientDetails(clientDetails);
        cacheAndGetClient(clientDetails.getClientId());
    }

    @Override
    public void updateClientSecret(String clientId, String secret) {
        super.updateClientSecret(clientId, secret);
        cacheAndGetClient(clientId);
    }

    @Override
    public void removeClientDetails(String clientId) {
        super.removeClientDetails(clientId);
        removeRedisCache(clientId);
    }

    /**
     * 删除redis缓存
     *
     * @param clientId
     */
    private void removeRedisCache(String clientId) {
        redisTemplate.delete(clientRedisKey(clientId));
    }

    /**
     * 将oauth_client_details全表刷入redis
     */
    public void loadAllClientToCache() {
        List<ClientDetails> list = super.listClientDetails();
        if (CollectionUtils.isEmpty(list)) {
            log.error("oauth_client_details表数据为空，请检查");
            return;
        }

        list.parallelStream().forEach(client -> redisTemplate.opsForValue().set(clientRedisKey(client.getClientId()), client));
    }

    private String clientRedisKey(String clientId) {
        return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
    }
}
