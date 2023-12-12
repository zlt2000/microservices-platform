package com.central.oauth2.common.config;

import cn.hutool.extra.spring.SpringUtil;
import com.central.oauth2.common.component.CustomAuthorizationServiceIntrospector;
import com.central.oauth2.common.component.CustomBearerTokenResolver;
import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.annotation.Resource;

/**
 * @author zlt
 * @version 1.0
 * @date 2023/12/5
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@EnableWebSecurity
@Import(BaseSecurityConfig.class)
public class DefaultResourceServerConf {
    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Resource
    private CustomBearerTokenResolver customBearerTokenResolver;

    @Resource
    private AccessDeniedHandler oAuth2AccessDeniedHandler;

    @Bean
    public SecurityFilterChain springSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> {
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = authorizeRequests
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    .requestMatchers(securityProperties.getIgnore().getUrls()).permitAll()
                    .anyRequest();
            this.setAuthenticate(authorizedUrl);
        }).headers(header -> header
                // 避免iframe同源无法登录许iframe
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        ).csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        // 资源服务配置
        http.oauth2ResourceServer(oauth2 -> {
            oauth2.authenticationEntryPoint(authenticationEntryPoint)
                    .bearerTokenResolver(customBearerTokenResolver)
                    .accessDeniedHandler(oAuth2AccessDeniedHandler)
                    .opaqueToken(token -> token.introspector(this.getOpaqueTokenIntrospector()));
        });

        return http.build();
    }

    protected OpaqueTokenIntrospector getOpaqueTokenIntrospector() {
        OAuth2AuthorizationService authorizationService = SpringUtil.getBean(OAuth2AuthorizationService.class);
        return new CustomAuthorizationServiceIntrospector(authorizationService);
    }

    /**
     * url权限控制，默认是认证就通过，可以重写实现个性化
     */
    protected void setAuthenticate(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl) {
        authorizedUrl.authenticated();
    }
}
