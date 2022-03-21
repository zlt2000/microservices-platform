package com.central.oauth.listener;

import com.central.common.constant.SecurityConstants;
import com.central.common.redis.template.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Component;

/**
 *
 * redis过期key监听器
 * @author zlt
 *
 */
@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    @Autowired
    private RedisRepository redisRepository;
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    private final RedisConnectionFactory connectionFactory;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, RedisConnectionFactory connectionFactory) {
        super(listenerContainer);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (message == null) {
            log.debug("message不能为空");
            return;
        }
        //获取失效的的key
        String expiredKey = message.toString();
        if (StringUtils.isEmpty(expiredKey)) {
            log.debug("expiredKey不能为空");
            return;
        }
        String accesskey = expiredKey.substring(0, expiredKey.indexOf(":") + 1);
        if (!"access:".equals(accesskey)) {
            log.debug("非需要监听key,跳过");
            return;
        }
        String accessValue = expiredKey.substring(expiredKey.indexOf(":") + 1);
        // 分布式集群部署下防止一个过期被多个服务重复消费
        String qc = "qc:" + accessValue;
        String oldLock = redisRepository.getAndSet(qc, "1");
        if (StringUtils.isNotEmpty(oldLock) && "1".equals(oldLock)) {
            log.debug("其他节点已经处理了该数据，跳过");
            return;
        }
        byte[] accessBakKey = serializeKey(SecurityConstants.ACCESS_BAK + accessValue);
        byte[] authKey = serializeKey(SecurityConstants.REDIS_TOKEN_AUTH + accessValue);
        RedisConnection conn = getConnection();
        try {
            byte[] access = conn.get(accessBakKey);
            byte[] auth = conn.get(authKey);
            OAuth2Authentication authentication = deserializeAuthentication(auth);
            if (authentication != null) {
                byte[] unameKey = serializeKey(SecurityConstants.REDIS_UNAME_TO_ACCESS + getApprovalKey(authentication));
                byte[] clientId = serializeKey(SecurityConstants.REDIS_CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
                conn.openPipeline();
                conn.lRem(unameKey, 1, access);
                conn.lRem(clientId, 1, access);
                conn.closePipeline();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            conn.del(serializeKey(qc));
            conn.close();
        }

    }

    private byte[] serializeKey(String object) {
        return serialize("" + object);
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? ""
                : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }
}
