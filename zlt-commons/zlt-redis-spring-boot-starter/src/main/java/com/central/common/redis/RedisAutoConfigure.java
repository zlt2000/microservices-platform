package com.central.common.redis;

import com.central.common.redis.properties.CacheManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * redis 配置类
 *
 * @author zlt
 * @date 2018/11/6 11:02
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@EnableConfigurationProperties({RedisProperties.class, CacheManagerProperties.class})
@EnableCaching
public class RedisAutoConfigure {
    @Autowired
    private CacheManagerProperties cacheManagerProperties;

    @Bean
    public RedisSerializer<String> redisKeySerializer() {
        return RedisSerializer.string();
    }

    @Bean
    public RedisSerializer<Object> redisValueSerializer() {
        return RedisSerializer.java();
    }

    /**
     * RedisTemplate配置
     * @param factory
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory
            , RedisSerializer<String> redisKeySerializer, RedisSerializer<Object> redisValueSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        redisTemplate.setDefaultSerializer(redisValueSerializer);
        redisTemplate.setKeySerializer(redisKeySerializer);
        redisTemplate.setHashKeySerializer(redisKeySerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "cacheManager")
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory
            , RedisSerializer<String> redisKeySerializer, RedisSerializer<Object> redisValueSerializer) {
        RedisCacheConfiguration difConf = getDefConf(redisKeySerializer, redisValueSerializer).entryTtl(Duration.ofHours(1));

        //自定义的缓存过期时间配置
        int configSize = cacheManagerProperties.getConfigs() == null ? 0 : cacheManagerProperties.getConfigs().size();
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>(configSize);
        if (configSize > 0) {
            cacheManagerProperties.getConfigs().forEach(e -> {
                RedisCacheConfiguration conf = getDefConf(redisKeySerializer, redisValueSerializer).entryTtl(Duration.ofSeconds(e.getSecond()));
                redisCacheConfigurationMap.put(e.getKey(), conf);
            });
        }

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(difConf)
                .withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":" + method.getName() + ":");
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    private RedisCacheConfiguration getDefConf(RedisSerializer<String> redisKeySerializer, RedisSerializer<Object> redisValueSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "cache".concat(":").concat(cacheName).concat(":"))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisKeySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisValueSerializer));
    }
}
