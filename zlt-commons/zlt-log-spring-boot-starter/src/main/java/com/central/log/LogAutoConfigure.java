package com.central.log;

import com.central.log.properties.AuditLogProperties;
import com.central.log.properties.LogDbProperties;
import com.central.log.properties.TraceProperties;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 日志自动配置
 *
 * @author zlt
 * @date 2019/8/13
 */
@ComponentScan
@EnableConfigurationProperties({TraceProperties.class, AuditLogProperties.class})
public class LogAutoConfigure {
    /**
     * 日志数据库配置
     */
    @Configuration
    @ConditionalOnClass(HikariConfig.class)
    @EnableConfigurationProperties(LogDbProperties.class)
    public static class LogDbAutoConfigure {}
}
