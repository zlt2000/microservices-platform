package com.central.oauth2.common.component;

import com.central.oauth2.common.properties.PermitProperties;
import com.central.oauth2.common.properties.SecurityProperties;
import com.central.oauth2.common.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Arrays;

/**
 * @author: zlt
 * @date: 2023/11/8
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@ConditionalOnClass(HttpServletRequest.class)
@Component
public class CustomBearerTokenResolver implements BearerTokenResolver {
	private final boolean allowFormEncodedBodyParameter = false;

	private final boolean allowUriQueryParameter = true;

	private final String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

	private final PathMatcher pathMatcher = new AntPathMatcher();

	private final PermitProperties permitProperties;

	public CustomBearerTokenResolver(SecurityProperties securityProperties) {
		this.permitProperties = securityProperties.getIgnore();
	}

	@Override
	public String resolve(HttpServletRequest request) {
		boolean match = Arrays.stream(permitProperties.getUrls())
			.anyMatch(url -> pathMatcher.match(url, request.getRequestURI()));

		if (match) {
			return null;
		}

		final String authorizationHeaderToken = AuthUtils.extractHeaderToken(request);
		final String parameterToken = isParameterTokenSupportedForRequest(request)
				? resolveFromRequestParameters(request) : null;
		if (authorizationHeaderToken != null) {
			if (parameterToken != null) {
				final BearerTokenError error = BearerTokenErrors
					.invalidRequest("Found multiple bearer tokens in the request");
				throw new OAuth2AuthenticationException(error);
			}
			return authorizationHeaderToken;
		}
		if (parameterToken != null && isParameterTokenEnabledForRequest(request)) {
			return parameterToken;
		}
		return null;
	}

	private static String resolveFromRequestParameters(HttpServletRequest request) {
		String[] values = request.getParameterValues("access_token");
		if (values == null || values.length == 0) {
			return null;
		}
		if (values.length == 1) {
			return values[0];
		}
		BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
		throw new OAuth2AuthenticationException(error);
	}

	private boolean isParameterTokenSupportedForRequest(final HttpServletRequest request) {
		return (("POST".equals(request.getMethod())
				&& MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
				|| "GET".equals(request.getMethod()));
	}

	private boolean isParameterTokenEnabledForRequest(final HttpServletRequest request) {
		return ((this.allowFormEncodedBodyParameter && "POST".equals(request.getMethod())
				&& MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
				|| (this.allowUriQueryParameter && "GET".equals(request.getMethod())));
	}

}
