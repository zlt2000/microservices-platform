package com.central.oauth.config;

import com.central.common.config.DefaultPasswordConfig;
import com.central.oauth.component.LoginProcessSetTenantFilter;
import com.central.oauth.service.IValidateCodeService;
import com.central.oauth.service.impl.RedisSecurityContextRepository;
import com.central.oauth.service.impl.RegisteredClientService;
import com.central.oauth.service.impl.UserDetailServiceFactory;
import com.central.oauth.support.mobile.MobileAuthenticationConverter;
import com.central.oauth.support.mobile.MobileAuthenticationProvider;
import com.central.oauth.support.openid.OpenIdAuthenticationConverter;
import com.central.oauth.support.openid.OpenIdAuthenticationProvider;
import com.central.oauth.support.password.PasswordAuthenticationConverter;
import com.central.oauth.support.password.PasswordAuthenticationProvider;
import com.central.oauth.support.passwordCode.PasswordCodeAuthenticationConverter;
import com.central.oauth.support.passwordCode.PasswordCodeAuthenticationProvider;
import com.central.oauth2.common.component.CustomAuthorizationServiceIntrospector;
import com.central.oauth2.common.component.CustomBearerTokenResolver;
import com.central.oauth2.common.config.BaseSecurityConfig;
import com.central.oauth2.common.enums.TokenType;
import com.central.oauth2.common.properties.SecurityProperties;
import com.central.oauth2.common.util.OAuth2ConfigurerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Arrays;

/**
 * @author: zlt
 * @date: 2023/11/16
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Import({BaseSecurityConfig.class, DefaultPasswordConfig.class})
public class AuthorizationServerConfig {
    private final UserDetailServiceFactory userDetailsServiceFactory;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final CustomBearerTokenResolver customBearerTokenResolver;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final LogoutHandler logoutHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AccessDeniedHandler oAuth2AccessDeniedHandler;
    private final SecurityProperties securityProperties;
    private final IValidateCodeService validateCodeService;
    private final RegisteredClientService registeredClientService;
    private final RedisSecurityContextRepository securityContextRepository;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    /**
     * spring security 默认的安全策略
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> {
                    // 授权服务器关闭basic认证
                    authorizeRequests
                            .requestMatchers(securityProperties.getIgnore().getUrls()).permitAll()
                            .anyRequest().authenticated();
                });
        // 登录登出配置
        http.apply(new FormIdentityLoginConfigurer(logoutSuccessHandler, logoutHandler));

        // 登录的认证信息储存策略(redis)
        http.securityContext(context -> context.securityContextRepository(securityContextRepository));

        // 授权配置
        OAuth2AuthorizationService authorizationService = OAuth2ConfigurerUtils.getAuthorizationService(http);
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = OAuth2ConfigurerUtils.getTokenGenerator(http);

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.apply(authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> tokenEndpoint
                    // 注入自定义的授权认证Converter
                    .accessTokenRequestConverter(accessTokenRequestConverter())
                    // 登录失败处理器
                    .errorResponseHandler(authenticationFailureHandler)
                    // 登录成功处理器
                    .accessTokenResponseHandler(authenticationSuccessHandler)
                    // 自定义授权类型
                    .authenticationProvider(new PasswordAuthenticationProvider(authorizationService, tokenGenerator, userDetailsServiceFactory, passwordEncoder))
                    .authenticationProvider(new PasswordCodeAuthenticationProvider(authorizationService, tokenGenerator, userDetailsServiceFactory, passwordEncoder, validateCodeService))
                    .authenticationProvider(new OpenIdAuthenticationProvider(authorizationService, tokenGenerator, userDetailsServiceFactory))
                    .authenticationProvider(new MobileAuthenticationProvider(authorizationService, tokenGenerator, userDetailsServiceFactory, passwordEncoder))
            )
            // 开启OpenID Connect 1.0协议相关端点
            .oidc(Customizer.withDefaults())
        )
        // 应用鉴权
        .clientAuthentication(clientConfig -> clientConfig.errorResponseHandler(authenticationFailureHandler));
        // 指定授权服务
        http.apply(authorizationServerConfigurer
                .authorizationService(authorizationService)
                .authorizationEndpoint(endpoint -> endpoint
                        .errorResponseHandler(authenticationFailureHandler)
                ));

        // 资源服务配置
        http.oauth2ResourceServer(oauth2 -> {
            oauth2.authenticationEntryPoint(authenticationEntryPoint)
                    .bearerTokenResolver(customBearerTokenResolver)
                    .accessDeniedHandler(oAuth2AccessDeniedHandler);
            if (TokenType.JWT.getName().equals(securityProperties.getResourceServer().getTokenType())) {
                oauth2.jwt(Customizer.withDefaults());
            } else {
                oauth2.opaqueToken(token -> token.introspector(new CustomAuthorizationServiceIntrospector(authorizationService)));
            }
        });

        http.addFilterBefore(new LoginProcessSetTenantFilter(), UsernamePasswordAuthenticationFilter.class);

        // 缓存应用
        registeredClientService.loadAllClientToCache();

        return http.build();
    }

    private AuthenticationConverter accessTokenRequestConverter() {
        return new DelegatingAuthenticationConverter(Arrays.asList(
                new PasswordAuthenticationConverter(),
                new PasswordCodeAuthenticationConverter(),
                new OpenIdAuthenticationConverter(),
                new MobileAuthenticationConverter()));
    }
}
