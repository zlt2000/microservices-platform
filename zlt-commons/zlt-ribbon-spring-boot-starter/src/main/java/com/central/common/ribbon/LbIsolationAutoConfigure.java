package com.central.common.ribbon;

import com.central.common.constant.ConfigConstants;
import com.central.common.ribbon.config.RuleConfigure;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

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
@RibbonClients(defaultConfiguration = {RuleConfigure.class})
public class LbIsolationAutoConfigure {

}
