package com.central.log.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 审计日志配置
 *
 * @author zlt
 * @date 2020/2/3
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zlt.audit-log")
@RefreshScope
public class AuditLogProperties {
    /**
     * 是否开启审计日志
     */
    private Boolean enabled = false;
    /**
     * 日志记录类型(logger/redis/db/es)
     */
    private String logType;
}
