package com.central.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Token配置
 *
 * @author zlt
 * @version 1.0
 * @date 2021/5/19
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zlt.oauth2.token.store")
@RefreshScope
public class TokenStoreProperties {
    /**
     * token存储类型(redis/db/authJwt/resJwt)
     */
    private String type = "redis";
}
