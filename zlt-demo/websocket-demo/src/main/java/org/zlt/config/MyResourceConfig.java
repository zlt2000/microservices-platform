package org.zlt.config;

import com.central.oauth2.common.config.DefaultResourceServerConf;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * security资源服务器配置
 *
 * @author zlt
 * @version 1.0
 * @date 2022/5/9
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
@EnableResourceServer
public class MyResourceConfig extends DefaultResourceServerConf {
}
