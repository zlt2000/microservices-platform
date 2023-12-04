package com.central.common.redis.component;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.stereotype.Component;

/**
 * @author: zlt
 * @date: 2023/11/21
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Component
public class RedissonAutoConfigCustomizers implements RedissonAutoConfigurationCustomizer {
    @Override
    public void customize(Config configuration) {
        JsonJacksonCodec jacksonCodec = new JsonJacksonCodec();
        jacksonCodec.getObjectMapper().registerModule(new JavaTimeModule());
        configuration.setCodec(jacksonCodec);
    }
}
