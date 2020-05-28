package com.central.common.ribbon.config;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.central.common.ribbon.rule.VersionIsolationRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author zlt
 * @date 2020/4/24
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class RuleConfigure {
    @Bean
    @ConditionalOnClass(NacosServer.class)
    @ConditionalOnMissingBean
    public IRule versionIsolationRule() {
        return new VersionIsolationRule();
    }
}
