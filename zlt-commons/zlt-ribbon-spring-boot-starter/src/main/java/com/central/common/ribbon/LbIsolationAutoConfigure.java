package com.central.common.ribbon;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.central.common.constant.ConfigConstants;
import com.central.common.ribbon.rule.VersionIsolationRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 自定义负载均衡配置
 *
 * @author zlt
 * @date 2019/9/3
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@ConditionalOnProperty(value = ConfigConstants.CONFIG_RIBBON_ISOLATION_ENABLED, havingValue = "true")
@AutoConfigureBefore(RibbonClientConfiguration.class)
public class LbIsolationAutoConfigure {
    @Bean
    @ConditionalOnClass(NacosServer.class)
    @ConditionalOnMissingBean
    public IRule versionIsolationRule() {
        return new VersionIsolationRule();
    }
}
