package com.central.gateway.config;

import com.central.common.config.DefaultPasswordConfig;
import com.central.oauth2.common.annotation.EnableTokenStore;
import com.central.oauth2.common.config.DefaultResourceServerConf;
import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * @author zlt
 */
@Configuration
@EnableResourceServer
@EnableConfigurationProperties(SecurityProperties.class)
@EnableTokenStore
@Import({DefaultPasswordConfig.class})
public class ResourceServerConfiguration extends DefaultResourceServerConf {
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = setHttp(http)
                .authorizeRequests()
                .antMatchers(securityProperties.getAuth().getHttpUrls()).authenticated()
                .antMatchers(securityProperties.getIgnore().getUrls()).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest();
        setAuthenticate(authorizedUrl);
        //允许使用iframe 嵌套，避免swagger-ui 不被加载的问题
        http.headers()
                .frameOptions()
                .disable()
                .and()
                .csrf().disable();
    }

    @Override
    public HttpSecurity setAuthenticate(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl) {
        return authorizedUrl.access("@permissionService.hasPermission(request, authentication)").and();
    }
}