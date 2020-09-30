package com.central.oauth2.common.store;

import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 认证服务器使用Redis存取令牌
 * 注意: 需要配置redis参数
 *
 * @author zlt
 * @date 2018/7/25 9:36
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@ConditionalOnProperty(prefix = "zlt.oauth2.token.store", name = "type", havingValue = "redis", matchIfMissing = true)
public class AuthRedisTokenStore {
    @Bean
    public TokenStore tokenStore(RedisConnectionFactory connectionFactory, SecurityProperties securityProperties, RedisSerializer<Object> redisValueSerializer) {
        return new CustomRedisTokenStore(connectionFactory, securityProperties, redisValueSerializer);
    }
}
