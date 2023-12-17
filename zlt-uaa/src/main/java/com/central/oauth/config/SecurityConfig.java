package com.central.oauth.config;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.SecurityConstants;
import com.central.common.utils.ResponseUtil;
import com.central.oauth.component.CustomAccessTokenResponseHttpMessageConverter;
import com.central.oauth.component.CustomeOAuth2TokenCustomizer;
import com.central.oauth2.common.properties.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * 服务安全相关配置
 *
 * @author: zlt
 * @date: 2023/11/9
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
public class SecurityConfig {
	/**
	 * token内容增强
	 */
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(){
		return context -> {
			JwtClaimsSet.Builder claims = context.getClaims();
			claims.claim(SecurityConstants.LICENSE_NAME, SecurityConstants.PROJECT_LICENSE);
			// 客户端模式不返回具体用户信息
			if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType().getValue())) {
				return;
			}
			if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
				claims.claim(SecurityConstants.DETAILS_USER, context.getPrincipal().getPrincipal());
			} else if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
				claims.claim(IdTokenClaimNames.AUTH_TIME, Date.from(Instant.now()));
				StandardSessionIdGenerator standardSessionIdGenerator = new StandardSessionIdGenerator();
				claims.claim("sid", standardSessionIdGenerator.generateSessionId());
			}
		};
	}

	/**
	 * 令牌生成规则实现
	 */
	@Bean
	public OAuth2TokenGenerator oAuth2TokenGenerator(JwtEncoder jwtEncoder, OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer) {
		JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
		jwtGenerator.setJwtCustomizer(jwtTokenCustomizer);

		OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
		accessTokenGenerator.setAccessTokenCustomizer(new CustomeOAuth2TokenCustomizer());

		return new DelegatingOAuth2TokenGenerator(
				jwtGenerator,
				accessTokenGenerator,
				new OAuth2RefreshTokenGenerator()
		);
	}

	/**
	 * 授权服务信息配置
	 */
	@Bean
	public AuthorizationServerSettings authorizationServerSettings(SecurityProperties securityProperties) {
		return AuthorizationServerSettings.builder()
				.issuer(securityProperties.getAuth().getIssuer())
				.authorizationEndpoint(SecurityConstants.AUTH_CODE_URL)
				.tokenEndpoint(SecurityConstants.OAUTH_TOKEN_URL)
				.tokenIntrospectionEndpoint(SecurityConstants.OAUTH_CHECK_TOKEN_URL)
				.jwkSetEndpoint(SecurityConstants.OAUTH_JWKS_URL)
				.build();
	}

	/**
	 * 授权错误处理
	 */
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler(ObjectMapper objectMapper) {
		return (request, response, authException) -> {
			String msg = null;
			if (StrUtil.isNotEmpty(authException.getMessage())) {
				msg = authException.getMessage();
			} else if (authException instanceof OAuth2AuthenticationException exception) {
				msg = exception.getError().getErrorCode();
			}
			ResponseUtil.responseFailed(objectMapper, response, msg);
		};
	}

	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler(ObjectMapper objectMapper) {
		return (request, response, authentication) -> {
			OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
					(OAuth2AccessTokenAuthenticationToken) authentication;

			OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
			OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
			Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

			OAuth2AccessTokenResponse.Builder builder =
					OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
							.tokenType(accessToken.getTokenType())
							.scopes(accessToken.getScopes());
			if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
				builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
			}
			if (refreshToken != null) {
				builder.refreshToken(refreshToken.getTokenValue());
			}
			if (!CollectionUtils.isEmpty(additionalParameters)) {
				builder.additionalParameters(additionalParameters);
			}
			OAuth2AccessTokenResponse accessTokenResponse = builder.build();
			ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
			CustomAccessTokenResponseHttpMessageConverter converter = new CustomAccessTokenResponseHttpMessageConverter(objectMapper);
			converter.write(accessTokenResponse, null, httpResponse);
		};
	}
}
