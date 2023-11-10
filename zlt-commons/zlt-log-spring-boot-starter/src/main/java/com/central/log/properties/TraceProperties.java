package com.central.log.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 日志链路追踪配置
 *
 * @author zlt
 * @date 2019/8/13
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zlt.trace")
@RefreshScope
@Component
public class TraceProperties {
    /**
     * 是否开启日志链路追踪
     */
    private Boolean enable = false;
}
