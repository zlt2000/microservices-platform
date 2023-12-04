package com.central.oauth2.common.config;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.SecurityConstants;
import com.central.common.utils.ResponseUtil;
import com.central.common.utils.WebfluxResponseUtil;
import com.central.oauth2.common.enums.TokenType;
import com.central.oauth2.common.properties.SecurityProperties;
import com.central.oauth2.common.service.impl.RedisOAuth2AuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.UUID;


/**
 * @author zlt
 * @date: 2023/11/25
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class BaseSecurityConfig {
    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(SecurityProperties securityProperties, RedissonClient redisson) {
        String tokenType = securityProperties.getResourceServer().getTokenType();
        if (TokenType.MEMORY.getName().equals(tokenType)) {
            return new InMemoryOAuth2AuthorizationService();
        }
        return new RedisOAuth2AuthorizationService(securityProperties, redisson);
    }

    /**
     * 鉴权错误处理
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> ResponseUtil.responseFailed(objectMapper, response, authException.getMessage());
    }
    @Bean
    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return (exchange, e) -> WebfluxResponseUtil.responseFailed(exchange, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    /**
     * 越权错误处理
     */
    @Bean
    public AccessDeniedHandler oAuth2AccessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, authException) -> ResponseUtil.responseFailed(objectMapper, response, authException.getMessage());
    }
    @Bean
    public ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return (exchange, e) -> WebfluxResponseUtil.responseFailed(exchange, HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    /**
     * 配置jwk源，使用非对称加密，公开用于检索匹配指定选择器的JWK的方法
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(RedissonClient redisson) throws ParseException {
        RBucket<String> rBucket = redisson.getBucket(SecurityConstants.AUTHORIZATION_JWS_PREFIX_KEY);
        String jwkSetCache = rBucket.get();
        JWKSet jwkSet;
        // 多个服务共用同一个 jwkSource 对象
        if (StrUtil.isEmpty(jwkSetCache)) {
            KeyPair keyPair = generateRsaKey();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
            jwkSet = new JWKSet(rsaKey);
            String jwkSetString = jwkSet.toString(Boolean.FALSE);
            // 缓存
            boolean success = rBucket.setIfAbsent(jwkSetString);
            if (!success) {// 防止同时启动服务时创建了多个jwks
                jwkSetCache = rBucket.get();
                jwkSet = JWKSet.parse(jwkSetCache);
            }
        } else {
            // 解析存储的jws
            jwkSet = JWKSet.parse(jwkSetCache);
        }
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
