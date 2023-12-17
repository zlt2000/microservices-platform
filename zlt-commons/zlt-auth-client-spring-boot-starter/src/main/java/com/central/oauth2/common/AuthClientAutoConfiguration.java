package com.central.oauth2.common;

import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 鉴权自动配置
 *
 * @author zlt
 * @version 1.0
 * @date 2021/7/24
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@EnableConfigurationProperties({SecurityProperties.class})
@ComponentScan
public class AuthClientAutoConfiguration {
}
