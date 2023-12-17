package com.central.oauth2.common.service.impl;

import com.central.common.constant.SecurityConstants;
import com.central.oauth2.common.pojo.ClientDto;
import com.central.oauth2.common.properties.SecurityProperties;
import com.central.oauth2.common.token.CustomAccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 实现 Redis 存储 OAuth2Authorization 对象
 * 1. 自动续签token（可配置是否开启）
 * 2. 增加 client_id_to_access 和 uname_to_access 两个集合，用于查看在线用户
 * 3. 增加过期监听，删除 client_id_to_access 和 uname_to_access 集合的值
 *
 * @author: zlt
 * @date: 2023/11/9
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {
	private final static SerializationCodec AUTH_CODEC = new SerializationCodec();
	private final static Long STATE_TIMEOUT_MINUTES = 10L;

	private static final String AUTHORIZATION = "token";

	private final SecurityProperties securityProperties;

	private final RedissonClient redisson;

	private final ExpiredObjectListener expiredObjectListener = new ExpiredObjectListener() {
		private final static String KEY_SPLIT = "::";
		/**
		 * redis 失效监听，删除集合里的值
		 * 在 redis.conf 文件中，确保以下设置是启用的：notify-keyspace-events Ex
		 * 监听的key格式为：token::access_token::${clientId}::${username}::${access_token}
		 * 	例如：token::access_token::webApp::admin::q7ySjsjZoinAx0zUJADqV9P1VmCwYmQDdemwOuhDHjNXVmkzkvquMk_vUBX_Yz40JModOhUwixu-ICH5hti50-KXngBgkn_V1pxu-PZ1YGNkBIDXneE0z4WYx6uFIVHS
		 */
		@Override
		public void onExpired(String name) {
			String[] keyArr = name.split(KEY_SPLIT);
			if (keyArr.length == 5) {
				String clientId = keyArr[2];
				String username = keyArr[3];
				String accessToken = keyArr[4];
				redisListDelete(getClientIdListKey(clientId), accessToken);
				redisListDelete(getUnameListKey(clientId, username), accessToken);
			}
		}
	};

	@Override
	public void save(OAuth2Authorization authorization) {
		if (isState(authorization)) {
			String token = authorization.getAttribute("state");
			this.redisBucketSet(OAuth2ParameterNames.STATE, token, authorization, Duration.ofMinutes(STATE_TIMEOUT_MINUTES));
		}

		if (isCode(authorization)) {
			OAuth2AuthorizationCode authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class).getToken();
			Duration duration = this.getExpireSeconds(authorizationCode);
			this.redisBucketSet(OAuth2ParameterNames.CODE, authorizationCode.getTokenValue(), authorization, duration);
		}

		if (isRefreshToken(authorization)) {
			OAuth2RefreshToken refreshToken = authorization.getRefreshToken().getToken();
			Duration duration = this.getExpireSeconds(refreshToken);
			this.redisBucketSet(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue(), authorization, duration);
		}

		if (isAccessToken(authorization)) {
			Duration duration = this.saveAccessToken(authorization);
			OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
			// 应用id集合
			this.redisListAdd(this.getClientIdListKey(authorization), accessToken.getTokenValue(), duration);
			// 应用id + 登录名 集合
			this.redisListAdd(this.getUnameListKey(authorization), accessToken.getTokenValue(), duration);
		}
	}

	private Duration saveAccessToken(OAuth2Authorization authorization) {
		return this.saveAccessToken(authorization, false);
	}

	private Duration saveAccessToken(OAuth2Authorization authorization, boolean isRenew) {
		OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
		Duration duration = this.getExpireSeconds(accessToken);
		this.redisBucketSet(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getTokenValue(), authorization, duration);
		RBucket<Integer> listenKeyBucket = redisson.getBucket(buildListenerKey(
				OAuth2ParameterNames.ACCESS_TOKEN, authorization.getRegisteredClientId()
				, authorization.getPrincipalName(), accessToken.getTokenValue()));
		if (!isRenew) {
			listenKeyBucket.set(1, duration);
			listenKeyBucket.addListener(expiredObjectListener);
		} else {
			listenKeyBucket.expire(duration);
		}
		return duration;
	}

	@Override
	public void remove(OAuth2Authorization authorization) {
		Assert.notNull(authorization, "authorization cannot be null");

		List<String> keys = new ArrayList<>();
		if (isState(authorization)) {
			String token = authorization.getAttribute("state");
			keys.add(buildKey(OAuth2ParameterNames.STATE, token));
		}

		if (isCode(authorization)) {
			OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
					.getToken(OAuth2AuthorizationCode.class);
			OAuth2AuthorizationCode authorizationCodeToken = authorizationCode.getToken();
			keys.add(buildKey(OAuth2ParameterNames.CODE, authorizationCodeToken.getTokenValue()));
		}

		if (isRefreshToken(authorization)) {
			OAuth2RefreshToken refreshToken = authorization.getRefreshToken().getToken();
			keys.add(buildKey(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue()));
		}

		if (isAccessToken(authorization)) {
			OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
			keys.add(buildKey(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getTokenValue()));
		}
		redisson.getKeys().delete(keys.toArray(String[]::new));
		this.removeList(authorization);
	}

	@Override
	@Nullable
	public OAuth2Authorization findById(String id) {
		return this.findByToken(id, OAuth2TokenType.ACCESS_TOKEN);
	}

	@Override
	@Nullable
	public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
		Assert.hasText(token, "token cannot be empty");
		if (tokenType == null) {
			tokenType = OAuth2TokenType.ACCESS_TOKEN;
		}
		OAuth2Authorization authorization = this.redisBucketGet(tokenType.getValue(), token);

		boolean isRenew = securityProperties.getAuth().getRenew().getEnable();
		if (isRenew && tokenType.equals(OAuth2TokenType.ACCESS_TOKEN) && authorization != null) {
			this.renew(authorization);
		}

		return authorization;
	}

	public void remove(String accessToken) {
		OAuth2Authorization oAuth2Authorization = this.findById(accessToken);
		this.remove(oAuth2Authorization);
	}

	public List<String> findTokensByClientId(String clientId) {
		RList<String> rList = redisson.getList(this.getClientIdListKey(clientId));
		return rList.readAll();
	}

	public List<String> findTokensByClientIdAndUserName(String clientId, String username) {
		RList<String> rList = redisson.getList(this.getUnameListKey(clientId, username));
		return rList.readAll();
	}

	private String buildKey(String type, String id) {
		return String.format("%s::%s::%s", AUTHORIZATION, type, id);
	}

	private String buildListenerKey(String type, String clientId, String username, String tokenValue) {
		return String.format("%s::%s::%s::%s::%s", AUTHORIZATION, type, clientId, username, tokenValue);
	}

	private static boolean isState(OAuth2Authorization authorization) {
		return Objects.nonNull(authorization.getAttribute("state"));
	}

	private static boolean isCode(OAuth2Authorization authorization) {
		OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
				.getToken(OAuth2AuthorizationCode.class);
		return Objects.nonNull(authorizationCode);
	}

	private static boolean isRefreshToken(OAuth2Authorization authorization) {
		return Objects.nonNull(authorization.getRefreshToken());
	}

	private static boolean isAccessToken(OAuth2Authorization authorization) {
		return Objects.nonNull(authorization.getAccessToken());
	}

	/**
	 * 续签 access_token
	 */
	private void renew(OAuth2Authorization authorization) {
		// 判断当前应用是否需要自动续签
		if (checkRenewClientId(authorization.getRegisteredClientId())) {
			// 获取过期时长
			long validitySeconds = getAccessTokenValiditySeconds(authorization.getRegisteredClientId());
			if (validitySeconds > 0) {
				OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
				long expiresIn = ChronoUnit.SECONDS.between(Instant.now(), accessToken.getExpiresAt());
				double expiresRatio = expiresIn / (double) validitySeconds;
				// 判断是否需要续签，当前剩余时间小于过期时长的50%则续签
				if (expiresRatio <= securityProperties.getAuth().getRenew().getTimeRatio()) {
					// 更新AccessToken过期时间
					updateAccessTokenExpiresAt(authorization, validitySeconds);
				}
			}
		}
	}

	/**
	 * 判断应用自动续签是否满足白名单和黑名单的过滤逻辑
	 *
	 * @param clientId 应用id
	 * @return 是否满足
	 */
	private boolean checkRenewClientId(String clientId) {
		boolean result = true;
		//白名单
		List<String> includeClientIds = securityProperties.getAuth().getRenew().getIncludeClientIds();
		//黑名单
		List<String> exclusiveClientIds = securityProperties.getAuth().getRenew().getExclusiveClientIds();
		if (includeClientIds.size() > 0) {
			result = includeClientIds.contains(clientId);
		} else if (exclusiveClientIds.size() > 0) {
			result = !exclusiveClientIds.contains(clientId);
		}
		return result;
	}

	/**
	 * 获取token的总有效时长
	 *
	 * @param clientId 应用id
	 */
	private long getAccessTokenValiditySeconds(String clientId) {
		ClientDto clientDetails = (ClientDto)redisson.getBucket(clientRedisKey(clientId)).get();
		if (clientDetails != null) {
			return clientDetails.getAccessTokenValiditySeconds();
		}

		//返回默认值
		return SecurityConstants.ACCESS_TOKEN_VALIDITY_SECONDS;
	}

	/**
	 * 更新 access_token 的过期时间
	 */
	@SuppressWarnings("unchecked")
	private void updateAccessTokenExpiresAt(OAuth2Authorization authorization, long validitySeconds) {
		try {
			Class<?> authClazz = authorization.getClass();
			Field tokensField = authClazz.getDeclaredField("tokens");
			tokensField.setAccessible(true);

			Map<Class<? extends OAuth2Token>, OAuth2Authorization.Token<?>> tokens = (Map<Class<? extends OAuth2Token>, OAuth2Authorization.Token<?>>) tokensField.get(authorization);
			OAuth2Authorization.Token<OAuth2AccessToken> newToken = this.genateNewAccessToken(authorization.getAccessToken(), validitySeconds);
			Map<Class<? extends OAuth2Token>, OAuth2Authorization.Token<?>> newTokens = new HashMap<>(tokens);
			newTokens.put(OAuth2AccessToken.class, newToken);
			tokensField.set(authorization, Collections.unmodifiableMap(newTokens));
			this.saveAccessToken(authorization, true);
			// 更新集合中的过期时间
			this.updateListExpiresAt(this.getClientIdListKey(authorization), Duration.ofSeconds(validitySeconds));
			this.updateListExpiresAt(this.getUnameListKey(authorization), Duration.ofSeconds(validitySeconds));
		} catch (Exception e) {
			log.error("updateAccessTokenExpiresAt-error", e);
			throw new RuntimeException(e);
		}
	}


	private void removeList(OAuth2Authorization authorization) {
		this.redisListDelete(this.getClientIdListKey(authorization), authorization.getId());
		this.redisListDelete(this.getUnameListKey(authorization), authorization.getId());
	}

	private void updateListExpiresAt(String key, Duration duration) {
		redisson.getList(key).expire(duration);
	}

	private OAuth2Authorization.Token<OAuth2AccessToken> genateNewAccessToken(OAuth2Authorization.Token<OAuth2AccessToken> oldToken, long validitySeconds) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(Duration.ofSeconds(validitySeconds));

		OAuth2AccessToken oldAccessToken = oldToken.getToken();
		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				oldAccessToken.getTokenValue(), issuedAt,
				expiresAt, oldAccessToken.getScopes());
		return new CustomAccessToken(accessToken, oldToken.getMetadata());
	}

	private Duration getExpireSeconds(OAuth2Token oAuth2Token) {
		return Duration.ofSeconds(
				ChronoUnit.SECONDS.between(oAuth2Token.getIssuedAt(), oAuth2Token.getExpiresAt()));
	}

	private RBucket<OAuth2Authorization> redisBucketSet(String type, String id, OAuth2Authorization value, Duration duration) {
		RBucket<OAuth2Authorization> rBucket = redisson.getBucket(buildKey(type, id), AUTH_CODEC);
		rBucket.set(value, duration);
		return rBucket;
	}

	private String getClientIdListKey(OAuth2Authorization authorization) {
		return this.getClientIdListKey(authorization.getRegisteredClientId());
	}
	private String getClientIdListKey(String clientId) {
		return buildKey(SecurityConstants.REDIS_CLIENT_ID_TO_ACCESS, clientId);
	}
	private String getUnameListKey(OAuth2Authorization authorization) {
		return this.getUnameListKey(authorization.getRegisteredClientId(), authorization.getPrincipalName());
	}
	private String getUnameListKey(String clientId, String username) {
		return buildKey(SecurityConstants.REDIS_UNAME_TO_ACCESS, clientId + "::" + username);
	}

	private OAuth2Authorization redisBucketGet(String type, String id) {
		return (OAuth2Authorization)redisson.getBucket(buildKey(type, id), AUTH_CODEC).get();
	}

	private RList<String> redisListAdd(String key, String value, Duration duration) {
		RList<String> rList = redisson.getList(key);
		rList.add(value);
		rList.expire(duration);
		return rList;
	}

	private RList<String> redisListDelete(String key, String value) {
		RList<String> rList = redisson.getList(key);
		rList.remove(value);
		return rList;
	}

	private String clientRedisKey(String clientId) {
		return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
	}
}
