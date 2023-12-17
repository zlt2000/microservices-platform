package com.central.oauth.service.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * @author: zlt
 * @date: 2023/11/10
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@RequiredArgsConstructor
@Component
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

	private final RedissonClient redissonClient;;

	private final static Long TIMEOUT = 10L;

	@Override
	public void save(OAuth2AuthorizationConsent authorizationConsent) {
		Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");

		redissonClient.getBucket(buildKey(authorizationConsent))
				.set(authorizationConsent, Duration.ofMinutes(TIMEOUT));
	}

	@Override
	public void remove(OAuth2AuthorizationConsent authorizationConsent) {
		Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
		redissonClient.getBucket(buildKey(authorizationConsent)).delete();
	}

	@Override
	public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
		Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
		Assert.hasText(principalName, "principalName cannot be empty");
		return (OAuth2AuthorizationConsent) redissonClient.getBucket(buildKey(registeredClientId, principalName)).get();
	}

	private static String buildKey(String registeredClientId, String principalName) {
		return "token:consent:" + registeredClientId + ":" + principalName;
	}

	private static String buildKey(OAuth2AuthorizationConsent authorizationConsent) {
		return buildKey(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
	}
}
