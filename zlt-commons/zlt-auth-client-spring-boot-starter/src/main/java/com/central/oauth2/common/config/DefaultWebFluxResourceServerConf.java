package com.central.oauth2.common.config;

import com.central.oauth2.common.component.CustomReactiveAuthorizationServiceIntrospector;
import com.central.oauth2.common.component.CustomServerBearerTokenAuthConverter;
import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

import javax.annotation.Resource;

/**
 * @author zlt
 * @version 1.0
 * @date 2023/12/5
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@EnableWebFluxSecurity
@Import(BaseSecurityConfig.class)
public class DefaultWebFluxResourceServerConf {
    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint;

    @Resource
    private ServerAccessDeniedHandler serverAccessDeniedHandler;

    @Autowired(required = false)
    private ReactiveAuthorizationManager authorizationManager;

    @Autowired(required = false)
    private ServerAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .headers(hSpec -> hSpec.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .exceptionHandling(e -> {
                    e.authenticationEntryPoint(serverAuthenticationEntryPoint)
                            .accessDeniedHandler(serverAccessDeniedHandler);
                });

        http.authorizeExchange(exchange -> {
            if (securityProperties.getAuth().getHttpUrls().length > 0) {
                exchange.pathMatchers(securityProperties.getAuth().getHttpUrls()).authenticated();
            }
            if (securityProperties.getIgnore().getUrls().length > 0) {
                exchange.pathMatchers(securityProperties.getIgnore().getUrls()).permitAll();
            }
            exchange.pathMatchers(HttpMethod.OPTIONS).permitAll();
            // url 鉴权
            if (authorizationManager != null) {
                exchange.anyExchange().access(authorizationManager);
            } else {
                exchange.anyExchange().authenticated();
            }
        });

        http.addFilterAt(this.getAuthWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    protected AuthenticationWebFilter getAuthWebFilter() {
        AuthenticationWebFilter oauth2 = new AuthenticationWebFilter(this.getAuthenticationManager());
        oauth2.setServerAuthenticationConverter(this.getAuthenticationConverter());
        oauth2.setAuthenticationFailureHandler(this.getFailureHandler());
        if (successHandler != null) {
            oauth2.setAuthenticationSuccessHandler(successHandler);
        }
        return oauth2;
    }

    protected ReactiveAuthenticationManager getAuthenticationManager() {
        return new OpaqueTokenReactiveAuthenticationManager(this.getOpaqueTokenIntrospector());
    }

    protected ServerAuthenticationConverter getAuthenticationConverter() {
        CustomServerBearerTokenAuthConverter authenticationConverter =
                new CustomServerBearerTokenAuthConverter(securityProperties);
        authenticationConverter.setAllowUriQueryParameter(true);
        return authenticationConverter;
    }

    protected ReactiveOpaqueTokenIntrospector getOpaqueTokenIntrospector() {
        return new CustomReactiveAuthorizationServiceIntrospector();
    }

    protected ServerAuthenticationEntryPointFailureHandler getFailureHandler() {
        ServerAuthenticationEntryPointFailureHandler failureHandler =
                new ServerAuthenticationEntryPointFailureHandler(serverAuthenticationEntryPoint);
        failureHandler.setRethrowAuthenticationServiceException(false);
        return failureHandler;
    }
}
