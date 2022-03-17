package com.central.oauth2.common.store;

import com.central.common.constant.SecurityConstants;
import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 优化自Spring Security的RedisTokenStore
 * 1. 支持redis所有集群模式包括cluster模式
 * 2. 使用pipeline减少连接次数，提升性能
 * 3. 自动续签token（可配置是否开启）
 *
 * @author zlt
 * @date 2019/7/7
 */
public class CustomRedisTokenStore implements TokenStore {
    private static final String ACCESS = "access:";
    private static final String ACCESS_BAK = "access_bak:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";

    private static final boolean springDataRedis_2_0 = ClassUtils.isPresent(
            "org.springframework.data.redis.connection.RedisStandaloneConfiguration",
            RedisTokenStore.class.getClassLoader());

    private final RedisConnectionFactory connectionFactory;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    private String prefix = "";

    private Method redisConnectionSet_2_0;

    /**
     * 认证配置
     */
    private SecurityProperties securityProperties;

    /**
     * 业务redis的value序列化
     */
    private RedisSerializer<Object> redisValueSerializer;

    public CustomRedisTokenStore(RedisConnectionFactory connectionFactory, SecurityProperties securityProperties, RedisSerializer<Object> redisValueSerializer) {
        this.connectionFactory = connectionFactory;
        this.securityProperties = securityProperties;
        this.redisValueSerializer = redisValueSerializer;
        if (springDataRedis_2_0) {
            this.loadRedisConnectionMethods_2_0();
        }
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private void loadRedisConnectionMethods_2_0() {
        this.redisConnectionSet_2_0 = ReflectionUtils.findMethod(
                RedisConnection.class, "set", byte[].class, byte[].class);
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }

    private OAuth2AccessToken deserializeAccessToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2AccessToken.class);
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    private OAuth2RefreshToken deserializeRefreshToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2RefreshToken.class);
    }

    private ClientDetails deserializeClientDetails(byte[] bytes) {
        return (ClientDetails) redisValueSerializer.deserialize(bytes);
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        byte[] serializedKey = serializeKey(AUTH_TO_ACCESS + key);
        byte[] bytes;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
        if (accessToken != null) {
            OAuth2Authentication storedAuthentication = readAuthentication(accessToken.getValue());
            if ((storedAuthentication == null || !key.equals(authenticationKeyGenerator.extractKey(storedAuthentication)))) {
                // Keep the stores consistent (maybe the same user is
                // represented by this authentication but the details have
                // changed)
                storeAccessToken(accessToken, authentication);
            }

        }
        return accessToken;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        OAuth2Authentication auth2Authentication = readAuthentication(token.getValue());
        //是否开启token续签
        boolean isRenew = securityProperties.getAuth().getRenew().getEnable();
        if (isRenew && auth2Authentication != null) {
            OAuth2Request clientAuth = auth2Authentication.getOAuth2Request();
            //判断当前应用是否需要自动续签
            if (checkRenewClientId(clientAuth.getClientId())) {
                //获取过期时长
                int validitySeconds = getAccessTokenValiditySeconds(clientAuth.getClientId());
                if (validitySeconds > 0) {
                    double expiresRatio = token.getExpiresIn() / (double) validitySeconds;
                    //判断是否需要续签，当前剩余时间小于过期时长的50%则续签
                    if (expiresRatio <= securityProperties.getAuth().getRenew().getTimeRatio()) {
                        //更新AccessToken过期时间
                        DefaultOAuth2AccessToken oAuth2AccessToken = (DefaultOAuth2AccessToken) token;
                        oAuth2AccessToken.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
                        storeAccessToken(oAuth2AccessToken, auth2Authentication, true);
                    }
                }
            }
        }
        return auth2Authentication;
    }

    /**
     * 判断应用自动续签是否满足白名单和黑名单的过滤逻辑
     *
     * @param clientId 应用id
     * @return 是否满足
     */
    private boolean checkRenewClientId(String clientId) {
        boolean result = true;
        //白名单
        List<String> includeClientIds = securityProperties.getAuth().getRenew().getIncludeClientIds();
        //黑名单
        List<String> exclusiveClientIds = securityProperties.getAuth().getRenew().getExclusiveClientIds();
        if (includeClientIds.size() > 0) {
            result = includeClientIds.contains(clientId);
        } else if (exclusiveClientIds.size() > 0) {
            result = !exclusiveClientIds.contains(clientId);
        }
        return result;
    }

    /**
     * 获取token的总有效时长
     *
     * @param clientId 应用id
     */
    private int getAccessTokenValiditySeconds(String clientId) {
        RedisConnection conn = getConnection();
        byte[] bytes;
        try {
            bytes = conn.get(serializeKey(SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId));
        } finally {
            conn.close();
        }
        if (bytes != null) {
            ClientDetails clientDetails = deserializeClientDetails(bytes);
            if (clientDetails.getAccessTokenValiditySeconds() != null) {
                return clientDetails.getAccessTokenValiditySeconds();
            }
        }

        //返回默认值
        return SecurityConstants.ACCESS_TOKEN_VALIDITY_SECONDS;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        byte[] bytes;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializeKey(SecurityConstants.REDIS_TOKEN_AUTH + token));
        } finally {
            conn.close();
        }
        return deserializeAuthentication(bytes);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        RedisConnection conn = getConnection();
        try {
            byte[] bytes = conn.get(serializeKey(REFRESH_AUTH + token));
            return deserializeAuthentication(bytes);
        } finally {
            conn.close();
        }
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        storeAccessToken(token, authentication, false);
    }

    /**
     * 存储token
     *
     * @param isRenew 是否续签
     */
    private void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication, boolean isRenew) {
        byte[] serializedAccessToken = serialize(token);
        byte[] serializedAuth = serialize(authentication);
        byte[] accessKey = serializeKey(ACCESS + token.getValue());
        byte[] accessBakKey = serializeKey(ACCESS_BAK + token.getValue());
        byte[] authKey = serializeKey(SecurityConstants.REDIS_TOKEN_AUTH + token.getValue());
        byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication));
        byte[] approvalKey = serializeKey(SecurityConstants.REDIS_UNAME_TO_ACCESS + getApprovalKey(authentication));
        byte[] clientId = serializeKey(SecurityConstants.REDIS_CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());

        RedisConnection conn = getConnection();
        try {
            byte[] oldAccessToken = conn.get(accessKey);
            //如果token已存在，并且不是续签的话直接返回
            if (!isRenew && oldAccessToken != null) {
                return;
            }

            conn.openPipeline();
            if (springDataRedis_2_0) {
                try {
                    this.redisConnectionSet_2_0.invoke(conn, accessKey, serializedAccessToken);
                    this.redisConnectionSet_2_0.invoke(conn, accessBakKey, serializedAccessToken);
                    this.redisConnectionSet_2_0.invoke(conn, authKey, serializedAuth);
                    this.redisConnectionSet_2_0.invoke(conn, authToAccessKey, serializedAccessToken);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                conn.set(accessKey, serializedAccessToken);
                conn.set(accessBakKey, serializedAccessToken);
                conn.set(authKey, serializedAuth);
                conn.set(authToAccessKey, serializedAccessToken);
            }
            //如果是续签token，需要先删除集合里旧的值
            if (oldAccessToken != null) {
                if (!authentication.isClientOnly()) {
                    conn.lRem(approvalKey, 1, oldAccessToken);
                }
                conn.lRem(clientId, 1, oldAccessToken);
            }
            if (!authentication.isClientOnly()) {
                conn.rPush(approvalKey, serializedAccessToken);
            }
            conn.rPush(clientId, serializedAccessToken);
            if (token.getExpiration() != null) {
                int seconds = token.getExpiresIn();
                conn.expire(accessKey, seconds);
                conn.expire(accessBakKey, seconds + 60);
                conn.expire(authKey, seconds);
                conn.expire(authToAccessKey, seconds);
                conn.expire(clientId, seconds);
                conn.expire(approvalKey, seconds);
            }
            OAuth2RefreshToken refreshToken = token.getRefreshToken();
            if (refreshToken != null && refreshToken.getValue() != null) {
                byte[] refresh = serialize(token.getRefreshToken().getValue());
                byte[] auth = serialize(token.getValue());
                byte[] refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
                byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + token.getValue());
                if (springDataRedis_2_0) {
                    try {
                        this.redisConnectionSet_2_0.invoke(conn, refreshToAccessKey, auth);
                        this.redisConnectionSet_2_0.invoke(conn, accessToRefreshKey, refresh);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    conn.set(refreshToAccessKey, auth);
                    conn.set(accessToRefreshKey, refresh);
                }
                expireRefreshToken(refreshToken, conn, refreshToAccessKey, accessToRefreshKey);
            }
            conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? ""
                : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken accessToken) {
        removeAccessToken(accessToken.getValue());
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        byte[] key = serializeKey(ACCESS + tokenValue);
        byte[] bytes;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(key);
        } finally {
            conn.close();
        }
        return deserializeAccessToken(bytes);
    }

    public void removeAccessToken(String tokenValue) {
        byte[] accessKey = serializeKey(ACCESS + tokenValue);
        byte[] accessBakKey = serializeKey(ACCESS_BAK + tokenValue);
        byte[] authKey = serializeKey(SecurityConstants.REDIS_TOKEN_AUTH + tokenValue);
        byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        RedisConnection conn = getConnection();
        try {
            byte[] access = conn.get(accessKey);
            byte[] auth = conn.get(authKey);
            conn.openPipeline();
            conn.del(accessKey);
            conn.del(accessBakKey);
            conn.del(accessToRefreshKey);
            // Don't remove the refresh token - it's up to the caller to do that
            conn.del(authKey);
            conn.closePipeline();

            OAuth2Authentication authentication = deserializeAuthentication(auth);
            if (authentication != null) {
                String key = authenticationKeyGenerator.extractKey(authentication);
                byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + key);
                byte[] unameKey = serializeKey(SecurityConstants.REDIS_UNAME_TO_ACCESS + getApprovalKey(authentication));
                byte[] clientId = serializeKey(SecurityConstants.REDIS_CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
                conn.openPipeline();
                conn.del(authToAccessKey);
                conn.lRem(unameKey, 1, access);
                conn.lRem(clientId, 1, access);
                conn.del(serialize(ACCESS + key));
                conn.closePipeline();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        byte[] refreshKey = serializeKey(REFRESH + refreshToken.getValue());
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.getValue());
        byte[] serializedRefreshToken = serialize(refreshToken);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            if (springDataRedis_2_0) {
                try {
                    this.redisConnectionSet_2_0.invoke(conn, refreshKey, serializedRefreshToken);
                    this.redisConnectionSet_2_0.invoke(conn, refreshAuthKey, serialize(authentication));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                conn.set(refreshKey, serializedRefreshToken);
                conn.set(refreshAuthKey, serialize(authentication));
            }
            expireRefreshToken(refreshToken, conn, refreshKey, refreshAuthKey);
            conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    private void expireRefreshToken(OAuth2RefreshToken refreshToken, RedisConnection conn, byte[] refreshKey, byte[] refreshAuthKey) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
            Date expiration = expiringRefreshToken.getExpiration();
            if (expiration != null) {
                int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                        .intValue();
                conn.expire(refreshKey, seconds);
                conn.expire(refreshAuthKey, seconds);
            }
        }
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        byte[] key = serializeKey(REFRESH + tokenValue);
        byte[] bytes;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(key);
        } finally {
            conn.close();
        }
        return deserializeRefreshToken(bytes);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }

    public void removeRefreshToken(String tokenValue) {
        byte[] refreshKey = serializeKey(REFRESH + tokenValue);
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue);
        byte[] refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue);
        byte[] access2RefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.del(refreshKey);
            conn.del(refreshAuthKey);
            conn.del(refresh2AccessKey);
            conn.del(access2RefreshKey);
            conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {
        byte[] key = serializeKey(REFRESH_TO_ACCESS + refreshToken);
        RedisConnection conn = getConnection();
        byte[] bytes = null;
        try {
            bytes = conn.get(key);
            conn.del(key);
        } finally {
            conn.close();
        }
        if (bytes == null) {
            return;
        }
        String accessToken = deserializeString(bytes);
        if (accessToken != null) {
            removeAccessToken(accessToken);
        }
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        byte[] approvalKey = serializeKey(SecurityConstants.REDIS_UNAME_TO_ACCESS + getApprovalKey(clientId, userName));
        List<byte[]> byteList;
        RedisConnection conn = getConnection();
        try {
            byteList = conn.lRange(approvalKey, 0, -1);
        } finally {
            conn.close();
        }
        return getTokenCollections(byteList);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        byte[] key = serializeKey(SecurityConstants.REDIS_CLIENT_ID_TO_ACCESS + clientId);
        List<byte[]> byteList;
        RedisConnection conn = getConnection();
        try {
            byteList = conn.lRange(key, 0, -1);
        } finally {
            conn.close();
        }
        return getTokenCollections(byteList);
    }

    private Collection<OAuth2AccessToken> getTokenCollections(List<byte[]> byteList) {
        if (byteList == null || byteList.size() == 0) {
            return Collections.emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<>(byteList.size());
        for (byte[] bytes : byteList) {
            OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
            accessTokens.add(accessToken);
        }
        return Collections.unmodifiableCollection(accessTokens);
    }
}
