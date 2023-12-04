package com.central.gateway.config;

import com.central.gateway.auth.*;
import com.central.oauth2.common.component.CustomReactiveAuthorizationServiceIntrospector;
import com.central.oauth2.common.config.BaseSecurityConfig;
import com.central.oauth2.common.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

/**
 * 资源服务器
 *
 * @author: zlt
 * @date: 2023/11/25
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
@EnableWebFluxSecurity
@Slf4j
@Import(BaseSecurityConfig.class)
public class ResourceServerConfiguration {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http
            , SecurityProperties securityProperties, PermissionAuthManager permissionAuthManager
            , ServerAuthenticationEntryPoint authenticationEntryPoint, ServerAccessDeniedHandler accessDeniedHandler) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .headers(hSpec -> hSpec.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .exceptionHandling(e -> {
                    e.authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler);
                });

        http.authorizeExchange(exchange -> {
            if (securityProperties.getAuth().getHttpUrls().length > 0) {
                exchange.pathMatchers(securityProperties.getAuth().getHttpUrls()).authenticated();
            }
            if (securityProperties.getIgnore().getUrls().length > 0) {
                exchange.pathMatchers(securityProperties.getIgnore().getUrls()).permitAll();
            }
            exchange.pathMatchers(HttpMethod.OPTIONS).permitAll()
                    .anyExchange().access(permissionAuthManager);
        });

        http.addFilterAt(this.getAuthWebFilter(authenticationEntryPoint)
                , SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    private AuthenticationWebFilter getAuthWebFilter(ServerAuthenticationEntryPoint authenticationEntryPoint) {
        OpaqueTokenReactiveAuthenticationManager authenticationManager =
                new OpaqueTokenReactiveAuthenticationManager(
                        new CustomReactiveAuthorizationServiceIntrospector());
        ServerBearerTokenAuthenticationConverter authenticationConverter = new ServerBearerTokenAuthenticationConverter();
        authenticationConverter.setAllowUriQueryParameter(true);

        AuthenticationWebFilter oauth2 = new AuthenticationWebFilter(authenticationManager);
        oauth2.setServerAuthenticationConverter(authenticationConverter);
        oauth2.setAuthenticationFailureHandler(getFailureHandler(authenticationEntryPoint));
        oauth2.setAuthenticationSuccessHandler(new Oauth2ServerAuthSuccessHandler());
        return oauth2;
    }

    private ServerAuthenticationEntryPointFailureHandler getFailureHandler(ServerAuthenticationEntryPoint authenticationEntryPoint) {
        ServerAuthenticationEntryPointFailureHandler failureHandler =
                new ServerAuthenticationEntryPointFailureHandler(authenticationEntryPoint);
        failureHandler.setRethrowAuthenticationServiceException(false);
        return failureHandler;
    }
}
