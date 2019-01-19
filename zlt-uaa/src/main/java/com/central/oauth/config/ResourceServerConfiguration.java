package com.central.oauth.config;

import com.central.oauth2.common.config.TokenStoreConfig;
import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 资源服务器配置
 *
 * @author zlt
 * @date 2018/10/27
 */
@Configuration
@EnableResourceServer
@Import(TokenStoreConfig.class)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //允许使用iframe 嵌套，避免swagger-ui 不被加载的问题
        http.headers().frameOptions().disable()
                    .and()
                .requestMatcher(request -> false)
                .authorizeRequests()
                .antMatchers(securityProperties.getIgnore().getUrls()).permitAll()
                .anyRequest()
                .authenticated();
    }
}
